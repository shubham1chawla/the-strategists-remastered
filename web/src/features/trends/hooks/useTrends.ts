import { useMemo } from 'react';
import { useSelector } from 'react-redux';
import { State } from '@/store';
import { LandTrend, PlayerTrend } from '@trends/state';

const useTrends = () => {
  const trends = useSelector((state: State) => state.trends);

  // Extracting player trends
  const playerTrends = useMemo(
    () => trends.filter(({ playerId }) => !!playerId) as PlayerTrend[],
    [trends],
  );

  // Extracting land trends
  const landTrends = useMemo(
    () => trends.filter(({ landId }) => !!landId) as LandTrend[],
    [trends],
  );

  return { trends, playerTrends, landTrends };
};

export default useTrends;
