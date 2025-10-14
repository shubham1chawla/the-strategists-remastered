import { useCallback, useMemo } from 'react';
import useGameState from '@game/hooks/useGameState';
import { Player } from '@game/state';
import { PlayerPrediction } from '@predictions/state';
import useTrendsState from '@trends/hooks/useTrendsState';
import usePredictionsState from './usePredictionsState';

interface PredictionsChartItem {
  [key: string]: any;
  step: number;
  key?: string;
  method: 'PREDICTION' | 'NETWORTH';
}

const usePredictionsChartItems = (player: Player) => {
  const predictions = usePredictionsState();
  const { playerTrends } = useTrendsState();
  const { players } = useGameState();

  // Utility method to collect by step and player ID
  const collectByStepAndPlayerId = useCallback(
    <T extends { playerId: number; step: number }>(list: T[]) =>
      list.reduce((stepMap, element) => {
        const playerMap = stepMap.get(element.step) || new Map<number, T>();
        playerMap.set(element.playerId, element);
        stepMap.set(element.step, playerMap);
        return stepMap;
      }, new Map<number, Map<number, T>>()),
    [],
  );

  // Utility method to add dummy player record if missing
  const addMissingPlayers = useCallback(
    <T extends { playerId: number; step: number }>(
      map: Map<number, Map<number, T>>,
      dummy: T,
    ): void => {
      map.forEach((playerMap, step) => {
        players.forEach((p) => {
          if (playerMap.has(p.id)) return;
          playerMap.set(p.id, {
            ...dummy,
            playerId: p.id,
            step,
          });
        });
      });
    },
    [players],
  );

  // Extracting step-wise player trends
  const stepWisePlayerTrends = useMemo(() => {
    const collection = collectByStepAndPlayerId(playerTrends);
    addMissingPlayers(collection, {
      cash: 0,
      netWorth: 0,
      playerId: -1,
      step: -1,
    });
    return collection;
  }, [playerTrends, collectByStepAndPlayerId, addMissingPlayers]);

  // Extracting step-wise predictions
  const stepWisePredictions = useMemo(() => {
    const collection = collectByStepAndPlayerId(predictions);
    addMissingPlayers(collection, {
      winnerProbability: 0,
      bankruptProbability: 1,
      prediction: 'BANKRUPT',
      playerId: -1,
      step: -1,
    });
    return collection;
  }, [predictions, collectByStepAndPlayerId, addMissingPlayers]);

  // Calculating maximum steps for the current game
  const maxSteps = useMemo(
    () =>
      Array.from(stepWisePlayerTrends.keys()).reduce(
        (max, step) => Math.max(step, max),
        0,
      ),
    [stepWisePlayerTrends],
  );

  // Utility method to find the visualization method
  const getVisualizationMethod = useCallback(
    (
      playerPredictionMap?: Map<number, PlayerPrediction>,
    ): 'NETWORTH' | 'PREDICTION' => {
      if (!playerPredictionMap) return 'NETWORTH';
      const playerPredictions = Array.from(playerPredictionMap.values());
      playerPredictions.sort(
        (p1, p2) => p2.winnerProbability - p1.winnerProbability,
      );
      return playerPredictions.length > 1 &&
        playerPredictions[0].prediction === 'WINNER'
        ? 'PREDICTION'
        : 'NETWORTH';
    },
    [],
  );

  // Preparing visual prediction items
  const predictionsChartItems = useMemo(() => {
    const items: PredictionsChartItem[] = [];
    for (let step = 1; step <= maxSteps; step += 1) {
      // Determining whether to use NETWORTH or PREDICTION method for visualization
      const playerPredictionMap = stepWisePredictions.get(step);
      const playerTrendMap = stepWisePlayerTrends.get(step);
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
        step,
        key: `item-${step}`,
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
    maxSteps,
    player,
    players,
    stepWisePlayerTrends,
    stepWisePredictions,
  ]);
  return predictionsChartItems;
};

export default usePredictionsChartItems;
