import { useMemo } from 'react';
import { useSelector } from 'react-redux';
import { State } from '../store';
import { LandTrend, PlayerTrend, Trend } from './reducer';

export const useTrends = () => {
  const trends: Trend[] = useSelector((state: State) => state.trend);

  // Extracting player trends
  const playerTrends = useMemo(
    () => trends.filter(({ playerId }) => !!playerId) as PlayerTrend[],
    [trends]
  );

  // Extracting land trends
  const landTrends = useMemo(
    () => trends.filter(({ landId }) => !!landId) as LandTrend[],
    [trends]
  );

  return { trends, playerTrends, landTrends };
};
