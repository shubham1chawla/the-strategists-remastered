import { useMemo } from 'react';
import { useSelector } from 'react-redux';
import { State } from '../store';

export const useActivities = () => {
  const activity = useSelector((state: State) => state.activity);
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
