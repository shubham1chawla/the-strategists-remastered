import { useSelector } from 'react-redux';
import { State } from '../store';

export const useActivities = () => {
  const activity = useSelector((state: State) => state.activity);

  // Extracting filtered activities
  const filteredActivites = activity.activities.filter(({ type }) =>
    activity.subscribedTypes.includes(type)
  );

  return {
    ...activity,
    filteredActivites,
  };
};
