import { useMemo } from 'react';
import { useSelector } from 'react-redux';
import { StrategistsState } from '@/store';

const useActivitiesState = () => {
  const activity = useSelector(
    (state: StrategistsState) => state.activitiesState,
  );
  const { activities, subscribedTypes } = activity;

  // Extracting filtered activities
  const filteredActivites = useMemo(
    () => activities.filter(({ type }) => subscribedTypes.includes(type)),
    [activities, subscribedTypes],
  );

  return {
    ...activity,
    filteredActivites,
  };
};

export default useActivitiesState;
