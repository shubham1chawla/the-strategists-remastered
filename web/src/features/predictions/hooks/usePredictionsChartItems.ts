import { useCallback, useMemo } from 'react';
import useGame from '@game/hooks/useGame';
import { Player } from '@game/state';
import { Prediction } from '@predictions/state';
import useTrends from '@trends/hooks/useTrends';
import usePredictions from './usePredictions';

interface PredictionsChartItem {
  [key: string]: any;
  turn: number;
  key?: string;
  method: 'PREDICTION' | 'NETWORTH';
}

const usePredictionsChartItems = (player: Player) => {
  const predictions = usePredictions();
  const { playerTrends } = useTrends();
  const { players } = useGame();

  // Utility method to collect by turn and player ID
  const collectByTurnAndPlayerId = useCallback(
    <T extends { playerId: number; turn: number }>(list: T[]) =>
      list.reduce((turnMap, element) => {
        const playerMap = turnMap.get(element.turn) || new Map<number, T>();
        playerMap.set(element.playerId, element);
        turnMap.set(element.turn, playerMap);
        return turnMap;
      }, new Map<number, Map<number, T>>()),
    [],
  );

  // Utility method to add dummy player record if missing
  const addMissingPlayers = useCallback(
    <T extends { playerId: number; turn: number }>(
      map: Map<number, Map<number, T>>,
      dummy: T,
    ): void => {
      map.forEach((playerMap, turn) => {
        players.forEach((p) => {
          if (playerMap.has(p.id)) return;
          playerMap.set(p.id, {
            ...dummy,
            playerId: p.id,
            turn,
          });
        });
      });
    },
    [players],
  );

  // Extracting turn-wise player trends
  const turnWisePlayerTrends = useMemo(() => {
    const collection = collectByTurnAndPlayerId(playerTrends);
    addMissingPlayers(collection, {
      cash: 0,
      netWorth: 0,
      playerId: -1,
      turn: -1,
    });
    return collection;
  }, [playerTrends, collectByTurnAndPlayerId, addMissingPlayers]);

  // Extracting turn-wise predictions
  const turnWisePredictions = useMemo(() => {
    const collection = collectByTurnAndPlayerId(predictions);
    addMissingPlayers(collection, {
      winnerProbability: 0,
      bankruptProbability: 1,
      type: 'BANKRUPT',
      playerId: -1,
      turn: -1,
    });
    return collection;
  }, [predictions, collectByTurnAndPlayerId, addMissingPlayers]);

  // Calculating maximum turns for the current game
  const maxTurns = useMemo(
    () =>
      Array.from(turnWisePlayerTrends.keys()).reduce(
        (max, turn) => Math.max(turn, max),
        0,
      ),
    [turnWisePlayerTrends],
  );

  // Utility method to find the visualization method
  const getVisualizationMethod = useCallback(
    (
      playerPredictionMap?: Map<number, Prediction>,
    ): 'NETWORTH' | 'PREDICTION' => {
      if (!playerPredictionMap) return 'NETWORTH';
      const playerPredictions = Array.from(playerPredictionMap.values());
      playerPredictions.sort(
        (p1, p2) => p2.winnerProbability - p1.winnerProbability,
      );
      return playerPredictions.length > 1 &&
        playerPredictions[0].type === 'WINNER'
        ? 'PREDICTION'
        : 'NETWORTH';
    },
    [],
  );

  // Preparing visual prediction items
  const predictionsChartItems = useMemo(() => {
    const items: PredictionsChartItem[] = [];
    for (let turn = 1; turn <= maxTurns; turn += 1) {
      // Determining whether to use NETWORTH or PREDICTION method for visualization
      const playerPredictionMap = turnWisePredictions.get(turn);
      const playerTrendMap = turnWisePlayerTrends.get(turn);
      const method = getVisualizationMethod(playerPredictionMap);

      // Calculating total share size in the current turn
      const totalShareSize =
        method === 'PREDICTION'
          ? Array.from(playerPredictionMap?.values() || []).reduce(
              (share, { winnerProbability }) => share + winnerProbability,
              0,
            )
          : Array.from(playerTrendMap?.values() || []).reduce(
              (share, { netWorth }) => share + netWorth,
              0,
            );

      // Figuring visual prediction item
      const item: PredictionsChartItem = {
        turn,
        key: `item-${turn}`,
        method,
      };
      item[player.username] = 0;
      item.Opponents = 0;
      players.forEach((p) => {
        const value =
          method === 'PREDICTION'
            ? playerPredictionMap?.get(p.id)?.winnerProbability || 0
            : playerTrendMap?.get(p.id)?.netWorth || 0;
        if (player.id === p.id) {
          item[player.username] += value / totalShareSize;
        } else {
          item.Opponents += value / totalShareSize;
        }
      });
      items.push(item);
    }
    return items;
  }, [
    getVisualizationMethod,
    maxTurns,
    player,
    players,
    turnWisePlayerTrends,
    turnWisePredictions,
  ]);
  return predictionsChartItems;
};

export default usePredictionsChartItems;
