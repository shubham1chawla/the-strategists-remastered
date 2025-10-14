import json
import logging
import os
from decimal import Decimal, localcontext, ROUND_HALF_UP
from typing import Optional, List, Tuple, Dict, Any, Set

import pandas as pd

from predictions import constants

logger = logging.getLogger(__name__)


def preprocess_dataframe(df: pd.DataFrame, *, drop_columns: Optional[List[str]] = None) -> pd.DataFrame:
    # Dropping unwanted features
    if drop_columns:
        df = df[df.columns[~df.columns.isin(drop_columns)]]

    # Converting integer-based columns to double
    return df.astype({
        "ownership.count": "double",
        "debit.invest.count": "double",
        "debit.rent.count": "double",
        "debit.count": "double",
        "credit.rent.count": "double",
        "credit.count": "double",
    })


def _round(number: float) -> float:
    with localcontext() as ctx:
        ctx.rounding = ROUND_HALF_UP
        dec: Decimal = Decimal(str(number)).quantize(Decimal("0.00"))
        return float(dec)


def load_update_payloads(jsonl_path: str) -> List[Dict[str, Any]]:
    # Loading JSONL file
    with open(jsonl_path, "r") as file:
        text = file.read().strip() or ""

    # Checking if file is not empty
    lines = [line.strip() for line in text.split("\n") if line.strip()]
    if not lines:
        return []

    # Processing JSON lines
    update_payloads: List[Dict[str, Any]] = []
    for line in lines:
        if stripped_line := line.strip():
            update_payloads.append(json.loads(stripped_line))

    return update_payloads


def parse_update_payloads(
        update_payloads: List[Dict[str, Any]],
        *,
        export_timestamp: Optional[int] = None,
        inference=False,
) -> List[Dict[str, Any]]:
    # Shared state for a game
    game_code: str = ""
    lands: List[Dict[str, Any]] = []
    players_paid_rent_ids: Dict[int, Set[int]] = {}  # <- Keeping track of rent IDs already accounted for per player
    players_received_rent_ids: Dict[int, Set[int]] = {}  # <- Keeping track of rent IDs already accounted for per player
    bankrupted_player_ids: Set[int] = set()  # <- Keeping track of bankrupted players
    parsed_rows_dict: Dict[int, Dict[str, Any]] = {}  # <- Player ID -> CSV-like row
    has_skip_bankrupts = False  # <- Keeping track of whether any player in the game bankrupted because of skips

    # Utility method to add player to parsed_rows_dict
    def add_player(new_player: Dict[str, Any]):
        parsed_rows_dict[new_player["id"]] = {
            "game.export.timestamp": export_timestamp,
            "game.code": game_code,
            "game.bankruptcy-order": None,
            "player.id": new_player["id"],
            "player.base-cash": new_player["cash"],
            "player.state": new_player["state"],
            "ownership.total": 0.0,
            "ownership.count": 0,
            **{f"ownership.{l["name"]}": 0.0 for l in lands},
            "debit.total": 0.0,
            "debit.count": 0,
            "debit.invest.total": 0.0,
            "debit.invest.count": 0,
            **{f"debit.invest.{l["name"]}": 0.0 for l in lands},
            "debit.rent.total": 0.0,
            "debit.rent.count": 0,
            **{f"debit.rent.{l["name"]}": 0.0 for l in lands},
            "credit.total": 0.0,
            "credit.count": 0,
            "credit.rent.total": 0.0,
            "credit.rent.count": 0,
            **{f"credit.rent.{l["name"]}": 0.0 for l in lands},
        }

        # Adding player's entry to rent mappings
        players_paid_rent_ids[new_player["id"]] = set()
        players_received_rent_ids[new_player["id"]] = set()

    # Processing each update payload
    for update_payload in update_payloads:
        game_step, update_type, payload = update_payload["gameStep"], update_payload["type"], update_payload["payload"]
        match update_type:
            # Processing CREATE & RESET update type containing GameResponse
            case "CREATE" | "RESET":
                game, players = payload["game"], payload["players"]
                game_code = game_code or game["code"]
                lands = lands or payload["lands"]

                # Starting to convert update payload in CSV-like structure
                for player in players:
                    add_player(player)

            # Processing JOIN update type containing player who joined
            case "JOIN":
                if not parsed_rows_dict:
                    raise ValueError(f"Update type 'JOIN' can't be processed before 'CREATE' or 'RESET'!")
                add_player(payload)

            # Processing KICK update type containing ID of player who got kicked out of the game
            case "KICK":
                # Checking if we have a valid state
                if payload not in parsed_rows_dict:
                    raise ValueError(f"Player must be in game to be kicked! Game Step: {game_step}")

                # Removing player's entry from parsed_rows_dict
                parsed_rows_dict.pop(payload)

            # Processing INVEST update type containing land and players who got updated
            case "INVEST":
                land, players = payload["land"], payload["players"]
                turn_players = [player for player in players if player["turn"]]

                # Checking if we have valid state
                if not turn_players or len(turn_players) != 1:
                    raise ValueError(f"Only 1 player should have the turn! Game Step: {game_step}")

                # Player who made the investment
                turn_player = turn_players[0]

                # Checking if there is only one entry for Player <-> Land
                player_lands = [player_land for player_land in land["players"] if
                                player_land["playerId"] == turn_player["id"]]
                if len(player_lands) != 1:
                    raise ValueError(f"Only 1 PlayerLand entry should be there for INVEST! Game Step: {game_step}")

                # PlayerLand record for investment
                player_land = player_lands[0]

                # Updating land-specific values
                turn_player_parsed_row = parsed_rows_dict[turn_player["id"]]
                turn_player_parsed_row.update({
                    f"ownership.{land["name"]}": _round(player_land["ownership"]),
                    f"debit.invest.{land["name"]}": _round(player_land["buyAmount"]),
                })

                # Recalculating total ownerships and total investment debits
                turn_player_parsed_row.update({
                    "ownership.total": _round(sum([
                        turn_player_parsed_row[f"ownership.{land["name"]}"] for land in lands
                    ])),
                    "ownership.count": (sum([
                        1 for land in lands if turn_player_parsed_row[f"ownership.{land["name"]}"] > 0
                    ])),
                    "debit.invest.total": _round(sum([
                        turn_player_parsed_row[f"debit.invest.{land["name"]}"] for land in lands
                    ])),
                    "debit.invest.count": (sum([
                        1 for land in lands if turn_player_parsed_row[f"debit.invest.{land["name"]}"] > 0
                    ])),
                })

                # Recalculating total debits (adding rent debits)
                turn_player_parsed_row.update({
                    "debit.total": _round(
                        turn_player_parsed_row["debit.rent.total"] + turn_player_parsed_row["debit.invest.total"]
                    ),
                    "debit.count": (
                            turn_player_parsed_row["debit.rent.count"] + turn_player_parsed_row["debit.invest.count"]
                    ),
                })

            # Processing RENT update type containing players who got updated
            case "RENT":
                # Checking if we have valid state
                source_players = [player for player in payload if player["turn"]]
                if not source_players or len(source_players) != 1:
                    raise ValueError(f"Only 1 player should have the turn! Game Step: {game_step}")

                # Source player who paid the rent and target players who received the rents
                source_player = source_players[0]
                target_players = [player for player in payload if player["id"] != source_player["id"]]
                target_player_ids = set([target_player["id"] for target_player in target_players])

                # Checking if we have valid state
                if not target_players:
                    raise ValueError(f"At least 1 target player should be present! Game Step: {game_step}")

                # Checking if we have valid state
                if 1 + len(target_players) != len(payload):
                    raise ValueError(f"Source player + target == total players in payload! Game Step: {game_step}")

                # Figuring out what land source player paid rent for
                land = lands[source_player["index"]]

                # Updating source player's parsed row
                source_player_parsed_row = parsed_rows_dict[source_player["id"]]
                for rent in source_player["paidRents"]:
                    if (
                            rent["landId"] != land["id"] or
                            # ^ Checking if the rent is for the give land
                            rent["targetPlayerId"] not in target_player_ids or
                            # ^ Checking if rent is for the given target players
                            rent["id"] in players_paid_rent_ids[source_player["id"]]
                            # ^ Checking if we have not already accounted the rent
                    ):
                        continue

                    # Updating source player's parsed row
                    debit_rent_land_key = f"debit.rent.{land["name"]}"
                    source_player_parsed_row.update({
                        "debit.total": _round(
                            source_player_parsed_row["debit.total"] + rent["rentAmount"]
                        ),
                        "debit.count": source_player_parsed_row["debit.count"] + 1,
                        "debit.rent.total": _round(
                            source_player_parsed_row["debit.rent.total"] + rent["rentAmount"]
                        ),
                        "debit.rent.count": source_player_parsed_row["debit.rent.count"] + 1,
                        debit_rent_land_key: _round(
                            source_player_parsed_row[debit_rent_land_key] + rent["rentAmount"]
                        ),
                    })

                    # Adding rent ID to mappings
                    players_paid_rent_ids[source_player["id"]].add(rent["id"])

                # Updating target players parsed rows
                for target_player in target_players:
                    target_player_parsed_row = parsed_rows_dict[target_player["id"]]
                    for rent in target_player["receivedRents"]:
                        if (
                                rent["landId"] != land["id"] or
                                # ^ Checking if the rent is for the give land
                                rent["sourcePlayerId"] != source_player["id"] or
                                # ^ Checking if rent is from the given source player
                                rent["id"] in players_received_rent_ids[target_player["id"]]
                                # ^ Checking if we have not already accounted the rent
                        ):
                            continue

                        # Updating target player's parsed row
                        credit_rent_land_key = f"credit.rent.{land["name"]}"
                        target_player_parsed_row.update({
                            "credit.total": _round(
                                target_player_parsed_row["credit.total"] + rent["rentAmount"]
                            ),
                            "credit.count": target_player_parsed_row["credit.count"] + 1,
                            "credit.rent.total": _round(
                                target_player_parsed_row["credit.rent.total"] + rent["rentAmount"]
                            ),
                            "credit.rent.count": target_player_parsed_row["credit.rent.count"] + 1,
                            credit_rent_land_key: _round(
                                target_player_parsed_row[credit_rent_land_key] + rent["rentAmount"]
                            ),
                        })

                        # Adding rent ID to mappings
                        players_received_rent_ids[target_player["id"]].add(rent["id"])

            # Processing BANKRUPTCY update type containing players and lands that got updated
            case "BANKRUPTCY":
                players = payload["players"]
                turn_players = [player for player in players if player["turn"]]

                # Checking if we have valid state
                if not turn_players or len(turn_players) != 1:
                    raise ValueError(f"Only 1 player should have the turn! Game Step: {game_step}")

                # Bankrupted player
                bankrupted_player = turn_players[0]

                # Checking if we have a valid state
                bankrupted_player_id = bankrupted_player["id"]
                if bankrupted_player["id"] in bankrupted_player_ids:
                    raise ValueError(f"Player '{bankrupted_player_id}' already bankrupted! Game Step: {game_step}")
                if bankrupted_player["state"] != "BANKRUPT":
                    raise ValueError(f"Player '{bankrupted_player_id}''s state not 'BANKRUPT'! Game Step: {game_step}")

                bankrupted_player_ids.add(bankrupted_player_id)
                parsed_rows_dict[bankrupted_player_id].update({
                    "game.bankruptcy-order": len(bankrupted_player_ids),
                    "player.state": bankrupted_player["state"],
                })

            # Processing WIN update type containing player who won
            case "WIN":
                # Checking if we have valid state
                if payload["state"] != "ACTIVE":
                    raise ValueError(f"Winner must have 'ACTIVE' state! Game Step: {game_step}")

                parsed_rows_dict[payload["id"]].update({
                    "game.bankruptcy-order": len(bankrupted_player_ids) + 1,
                    "player.state": payload["state"],
                })

            # Processing SKIP update type containing player who got skipped
            case "SKIP":
                # Checking if skips are enabled
                if "remainingSkipsCount" not in payload:
                    pass

                # Checking if current player got bankrupted
                remaining_skips_count = payload["remainingSkipsCount"] or 0
                if not has_skip_bankrupts and remaining_skips_count <= 0:
                    has_skip_bankrupts = True

    # Converting parsed rows in a list and sorting based on bankruptcy order
    parsed_rows: List[Dict[str, Any]] = list(parsed_rows_dict.values())
    parsed_rows = list(sorted(parsed_rows, key=lambda row: row["game.bankruptcy-order"] or len(parsed_rows_dict)))

    # Validating parsed rows for training
    if not inference:
        # Checking if export timestamp is provided
        if not export_timestamp:
            raise ValueError("Export timestamp must be provided for training!")

        # Checking if there are more than players
        if len(parsed_rows) <= 1:
            raise ValueError("More than 1 player required!")

        # Checking any player got bankrupted because of skips
        if has_skip_bankrupts:
            raise ValueError("All players must have more than 0 remaining skips!")

        active_count = sum([1 for row in parsed_rows if row["player.state"] == "ACTIVE"])
        bankrupt_count = sum([1 for row in parsed_rows if row["player.state"] == "BANKRUPT"])

        # Checking if only 1 active player remained
        if active_count != 1:
            raise ValueError("Only 1 active player should remain!")

        # Checking bankrupt count
        if bankrupt_count != len(parsed_rows) - 1:
            raise ValueError("Apart from 1 active player, all other players should be bankrupt!")

        # Checking total counts
        if active_count + bankrupt_count != len(parsed_rows):
            raise ValueError("Active & bankrupt players count should add up to total players count!")

        # Checking bankruptcy order
        order = 1
        for row in parsed_rows:
            if row["game.bankruptcy-order"] != order:
                raise ValueError("Inconsistent bankruptcy order!")
            order += 1

        return parsed_rows

    # Adjusting parsed rows for inference
    else:
        # Selecting only ACTIVE players from parsed rows
        active_parsed_rows = [row for row in parsed_rows if row["player.state"] == "ACTIVE"]
        if len(active_parsed_rows) <= 1:
            raise ValueError("More than 1 active player required for inference!")

        # Setting ACTIVE players to have same bankruptcy order == length of players
        for row in active_parsed_rows:
            row["game.bankruptcy-order"] = len(parsed_rows)

        return active_parsed_rows


def _load_jsonl_files(game_map_id: str) -> Tuple[int, pd.DataFrame]:
    # Checking if history directory is set
    if constants.HISTORY_DATA_DIR not in os.environ:
        raise KeyError(f"Set '{constants.HISTORY_DATA_DIR}' to a directory containing history!")

    # Validating history directory
    history_dir = os.getenv(constants.HISTORY_DATA_DIR)
    if not os.path.exists(history_dir) or not os.path.isdir(history_dir):
        raise FileNotFoundError(f"'{history_dir}' either doesn't exist or is not a directory!")

    # Checking if JSONL files in the data directory
    logger.info(f"Loading JSONL files from: '{history_dir}'")
    jsonl_paths = [os.path.join(history_dir, name)
                   for name in os.listdir(history_dir) if name.startswith(game_map_id) and name.endswith(".jsonl")]

    # Checking if we found any JSONL file
    if not jsonl_paths:
        logger.warning(f"'{history_dir}' contains no '.jsonl' files for game map ID '{game_map_id}'!")
        return 0, pd.DataFrame()
    logger.info(f"Loaded {len(jsonl_paths)} JSONL files for game map ID: '{game_map_id}'")

    # Parsing JSONL files to make it compatible with CSV files
    parsed_rows: List[Dict[str, Any]] = []
    for jsonl_path in jsonl_paths:
        try:
            # Loading update payloads from JSONL file
            update_payloads = load_update_payloads(jsonl_path)

            # Converting update payloads in CSV-like structure
            export_timestamp = int(jsonl_path.replace(".jsonl", "").split("-")[-1])
            parsed_rows.extend(parse_update_payloads(
                update_payloads,
                export_timestamp=export_timestamp,
                inference=False,
            ))
        except Exception as e:
            logger.error(f"Ignoring to include '{jsonl_path}'! {e}")

    return len(jsonl_paths), pd.DataFrame(parsed_rows)


def _load_legacy_csv_files(game_map_id: str) -> Tuple[int, pd.DataFrame]:
    # Checking if legacy predictions data directory is set, only if we should include legacy data
    if constants.LEGACY_PREDICTIONS_DATA_DIR not in os.environ:
        return 0, pd.DataFrame()

    # Validating legacy predictions data directory
    legacy_predictions_data_dir = os.getenv(constants.LEGACY_PREDICTIONS_DATA_DIR)
    if not os.path.exists(legacy_predictions_data_dir) or not os.path.isdir(legacy_predictions_data_dir):
        raise FileNotFoundError(f"'{legacy_predictions_data_dir}' either doesn't exist or is not a directory!")

    # Checking if csv files in the legacy data directory
    logger.info(f"Loading CSV files from: '{legacy_predictions_data_dir}'")
    csv_paths = [os.path.join(legacy_predictions_data_dir, name)
                 for name in os.listdir(legacy_predictions_data_dir) if
                 name.startswith(game_map_id) and name.endswith(".csv")]

    # Checking if we found any CSV file
    if not csv_paths:
        logger.warning(f"'{legacy_predictions_data_dir}' contains no '.csv' files for game map ID '{game_map_id}'!")
        return 0, pd.DataFrame()
    logger.info(f"Loaded {len(csv_paths)} CSV files for game map ID: '{game_map_id}'")

    # Producing combined dataframe
    df = pd.concat([pd.read_csv(csv_path) for csv_path in csv_paths], ignore_index=True)

    # Combining csv files
    return len(csv_paths), df


def load_training_dataset(game_map_id: str) -> Tuple[int, pd.DataFrame]:
    # Loading data from CSV files
    csv_files_count, csv_df = _load_legacy_csv_files(game_map_id)
    logger.info(f"Total CSV files: {csv_files_count} | Total CSV rows: {csv_df.shape[0]}")

    # Loading data from JSONL files
    jsonl_files_count, jsonl_df = _load_jsonl_files(game_map_id)
    logger.info(f"Total JSONL files: {jsonl_files_count} | Total JSONL rows: {jsonl_df.shape[0]}")

    # Checking if we have any data to train the model
    if csv_files_count + jsonl_files_count == 0:
        raise ValueError(f"No CSV or JSONL files found!")

    # Combining dfs
    total_files_count = csv_files_count + jsonl_files_count
    df = pd.concat([csv_df, jsonl_df], ignore_index=True)

    # Removing duplicates (keeping jsonl rows which are added later)
    rows_before_dropping = df.shape[0]
    df = df.drop_duplicates(subset=df.columns[~df.columns.isin(["game.export.timestamp"])], keep="last")
    rows_after_dropping = df.shape[0]
    if rows_before_dropping != rows_after_dropping:
        logger.warning(f"Identified {rows_before_dropping - rows_after_dropping} duplicate rows!")

    # Preprocessing dataframe
    df = preprocess_dataframe(df, drop_columns=[
        # Removed commonly with inference
        "game.export.timestamp",
        "game.bankruptcy-order",

        # Removed only for training - inference will not have player.username column anymore
        # player.id is not removed here because it is used to make index when preparing mlflow example
        "player.username",
    ])

    logger.info(f"Total files: {total_files_count} | Total rows: {df.shape[0]}")
    return total_files_count, df
