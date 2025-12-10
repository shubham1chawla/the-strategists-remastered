import { useDispatch, useSelector } from 'react-redux';
import { StrategistsState } from '@/store';
import useNotifications from '@shared/hooks/useNotifications';
import { subscribedTypesSetted, UpdateType } from '@activities/state';

const useActivitiesState = () => {
  const { activities, subscribedTypes } = useSelector(
    (state: StrategistsState) => state.activitiesState,
  );
  const { infoNotification } = useNotifications();
  const dispatch = useDispatch();

  const formatUpdateType = (type: UpdateType): string => {
    return type.charAt(0) + type.slice(1).toLowerCase();
  };

  const setSubscribedTypes = (updateTypes: UpdateType[]) => {
    if (updateTypes.length > subscribedTypes.length) {
      const set = new Set<UpdateType>(subscribedTypes);
      const updateType = updateTypes.filter((type) => !set.has(type))[0];
      infoNotification({
        title: `Subscribed to all ${formatUpdateType(updateType)} activities!`,
      });
    } else {
      const set = new Set<UpdateType>(updateTypes);
      const updateType = subscribedTypes.filter((type) => !set.has(type))[0];
      infoNotification({
        title: `Unsubscribed from all ${formatUpdateType(updateType)} activities!`,
      });
    }
    dispatch(subscribedTypesSetted(updateTypes));
  };

  return {
    activities,
    subscribedTypes,
    formatUpdateType,
    setSubscribedTypes,
  };
};

export default useActivitiesState;
