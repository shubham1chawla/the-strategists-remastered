package com.strategists.game.csv.impl;

import com.strategists.game.csv.StrategistsCSV;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.PlayerLand;
import com.strategists.game.entity.Rent;
import com.strategists.game.util.MathUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class PredictionsCSV implements StrategistsCSV {

    @Getter
    @AllArgsConstructor
    public enum Header {

        // Game-related columns
        GAME_EXPORT_TIMESTAMP("game.export.timestamp", false),
        GAME_CODE("game.code", false),
        GAME_BANKRUPTCY_ORDER("game.bankruptcy-order", false),

        // Player-related columns
        PLAYER_ID("player.id", false),
        PLAYER_BASE_CASH("player.base-cash", false),
        PLAYER_STATE("player.state", false),

        // Ownership-related columns
        OWNERSHIP_TOTAL("ownership.total", false),
        OWNERSHIP_COUNT("ownership.count", false),
        OWNERSHIP_LAND_FMT("ownership.%s", true),

        // Debits-related columns
        DEBIT_TOTAL("debit.total", false),
        DEBIT_COUNT("debit.count", false),
        DEBIT_INVEST_TOTAL("debit.invest.total", false),
        DEBIT_INVEST_COUNT("debit.invest.count", false),
        DEBIT_INVEST_LAND_FMT("debit.invest.%s", true),
        DEBIT_RENT_TOTAL("debit.rent.total", false),
        DEBIT_RENT_COUNT("debit.rent.count", false),
        DEBIT_RENT_LAND_FMT("debit.rent.%s", true),

        // Credits-related columns
        CREDIT_TOTAL("credit.total", false),
        CREDIT_COUNT("credit.count", false),
        CREDIT_RENT_TOTAL("credit.rent.total", false),
        CREDIT_RENT_COUNT("credit.rent.count", false),
        CREDIT_RENT_LAND_FMT("credit.rent.%s", true);

        private final String value;
        private final boolean format;

        public static Header fromHeaderString(String headerString) {
            if (StringUtils.hasText(headerString)) {
                for (var header : Header.values()) {
                    var case1 = header.value.equals(headerString);
                    var case2 = header.format && headerString.startsWith(header.value.replace("%s", ""));
                    if (case1 || case2) {
                        return header;
                    }
                }
            }
            throw new IllegalArgumentException("Invalid header: " + headerString);
        }
    }

    private final long timestamp = System.currentTimeMillis();
    private final String defaultFileName;
    private final List<String> headers;
    private final List<Map<String, Object>> rowMaps;

    public PredictionsCSV(Game game, List<Land> lands, List<Player> players) {
        defaultFileName = String.format("%s-%s-%s", game.getGameMapId(), game.getCode(), timestamp);
        headers = getHeaders(lands);
        rowMaps = getRowMaps(game, players);
    }

    private List<String> getHeaders(List<Land> lands) {
        var headers = new ArrayList<String>();
        for (var header : Header.values()) {
            if (header.format) {
                for (var land : lands) {
                    headers.add(String.format(header.value, land.getName()));
                }
            } else {
                headers.add(header.value);
            }
        }
        return headers;
    }

    private List<Map<String, Object>> getRowMaps(Game game, List<Player> players) {
        var rowMaps = new ArrayList<Map<String, Object>>(players.size());
        for (int index = 0; index < players.size(); index++) {
            var values = new LinkedHashMap<String, Object>();
            var player = players.get(index);

            // Preparing statistics per land
            var investmentPerLand = player.getPlayerLands().stream().collect(Collectors.toMap(pl -> pl.getLand().getName(), Function.identity()));
            var rentsPaidPerLand = player.getPaidRents().stream().collect(Collectors.groupingBy(r -> r.getLand().getName()));
            var rentsReceivedPerLand = player.getReceivedRents().stream().collect(Collectors.groupingBy(r -> r.getLand().getName()));

            // Preparing statistics for the player
            var totalOwnership = MathUtil.sum(player.getPlayerLands(), PlayerLand::getOwnership);
            var totalInvestment = MathUtil.sum(player.getPlayerLands(), PlayerLand::getBuyAmount);
            var totalRentPaid = MathUtil.sum(player.getPaidRents(), Rent::getRentAmount);
            var totalRentReceived = MathUtil.sum(player.getReceivedRents(), Rent::getRentAmount);

            // Computing values
            for (var header : headers) {
                Object value = null;
                switch (Header.fromHeaderString(header)) {
                    case Header.GAME_EXPORT_TIMESTAMP:
                        value = timestamp;
                        break;
                    case Header.GAME_CODE:
                        value = game.getCode();
                        break;
                    case Header.GAME_BANKRUPTCY_ORDER:
                        value = index + 1;
                        break;
                    case Header.PLAYER_ID:
                        value = player.getId();
                        break;
                    case Header.PLAYER_BASE_CASH:
                        value = game.getPlayerBaseCash();
                        break;
                    case Header.PLAYER_STATE:
                        value = player.getState();
                        break;
                    case Header.OWNERSHIP_COUNT, Header.DEBIT_INVEST_COUNT:
                        value = investmentPerLand.size();
                        break;
                    case Header.OWNERSHIP_TOTAL:
                        value = totalOwnership;
                        break;
                    case Header.DEBIT_TOTAL:
                        value = MathUtil.round(totalInvestment + totalRentPaid);
                        break;
                    case Header.DEBIT_COUNT:
                        value = investmentPerLand.size() + player.getPaidRents().size();
                        break;
                    case Header.DEBIT_INVEST_TOTAL:
                        value = totalInvestment;
                        break;
                    case Header.DEBIT_RENT_TOTAL:
                        value = totalRentPaid;
                        break;
                    case Header.DEBIT_RENT_COUNT:
                        value = player.getPaidRents().size();
                        break;
                    case Header.CREDIT_TOTAL, Header.CREDIT_RENT_TOTAL:
                        value = totalRentReceived;
                        break;
                    case Header.CREDIT_COUNT, Header.CREDIT_RENT_COUNT:
                        value = player.getReceivedRents().size();
                        break;
                    default: {
                        val split = header.split("\\.");
                        val name = split[split.length - 1];
                        if ("ownership".equals(split[0])) {
                            value = investmentPerLand.containsKey(name) ? investmentPerLand.get(name).getOwnership() : 0d;
                        } else if ("credit".equals(split[0])) {
                            value = MathUtil.sum(rentsReceivedPerLand.get(name), Rent::getRentAmount);
                        } else if ("invest".equals(split[1])) {
                            value = investmentPerLand.containsKey(name) ? investmentPerLand.get(name).getBuyAmount() : 0d;
                        } else if ("rent".equals(split[1])) {
                            value = MathUtil.sum(rentsPaidPerLand.get(name), Rent::getRentAmount);
                        }
                    }
                }
                values.put(header, value);
            }
            rowMaps.add(values);
        }
        return rowMaps;
    }

}
