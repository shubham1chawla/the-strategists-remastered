import { Activity } from './reducer';

export namespace ActivityActions {
  export const Types = {
    SET_ACTIVITIES: 'SET_ACTIVITIES',
    ADD_ACTIVITY: 'ADD_ACTIVITY',
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
}
