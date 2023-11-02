import { Activity, ActivityType } from './reducer';

export namespace ActivityActions {
  export const Types = {
    SET_ACTIVITIES: 'SET_ACTIVITIES',
    ADD_ACTIVITY: 'ADD_ACTIVITY',
    SET_SUBSCRIBED_TYPES: 'SET_SUBSCRIBED_TYPES',
  };

  export const setActivities = (activities: Activity[]) => {
    return {
      type: Types.SET_ACTIVITIES,
      payload: activities,
    };
  };

  export const addActivity = (activity: Activity) => {
    return {
      type: Types.ADD_ACTIVITY,
      payload: activity,
    };
  };

  export const setSubscribedTypes = (types: ActivityType[]) => {
    return {
      type: Types.SET_SUBSCRIBED_TYPES,
      payload: types,
    };
  };
}
