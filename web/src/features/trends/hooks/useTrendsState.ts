import { useMemo } from 'react';
import { useSelector } from 'react-redux';
import { StrategistsState } from '@/store';
import { LandTrend, PlayerTrend } from '@trends/state';

const useTrendsState = () => {
  const trends = useSelector((state: StrategistsState) => state.trendsState);

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

export default useTrendsState;
