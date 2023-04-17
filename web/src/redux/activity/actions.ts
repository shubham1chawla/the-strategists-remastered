export namespace ActivityActions {
  export const Types = {
    SET_ACTIVITIES: 'SET_ACTIVITIES',
    ADD_ACTIVITY: 'ADD_ACTIVITY',
  };

  export const setActivities = (activities: string[]) => {
    return {
      type: Types.SET_ACTIVITIES,
      payload: activities,
    };
  };

  export const addActivity = (activity: string) => {
    return {
      type: Types.ADD_ACTIVITY,
      payload: activity,
    };
  };
}
