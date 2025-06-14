import { useMemo } from 'react';
import { useSelector } from 'react-redux';
import { State } from '@/store';

const useActivities = () => {
  const activity = useSelector((state: State) => state.activities);
  const { activities, subscribedTypes } = activity;

  // Extracting filtered activities
  const filteredActivites = useMemo(
    () => activities.filter(({ type }) => subscribedTypes.includes(type)),
    [activities, subscribedTypes]
  );

  return {
    ...activity,
    filteredActivites,
  };
};

export default useActivities;
