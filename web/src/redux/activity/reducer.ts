import { ActivityActions } from '.';

export type ActivityState = string[];

export const activityReducer = (
  state: ActivityState = [],
  action: any
): ActivityState => {
  const { type, payload } = action;
  switch (type) {
    case ActivityActions.Types.SET_ACTIVITIES:
      return [...payload];

    case ActivityActions.Types.ADD_ACTIVITY:
      return [payload, ...state];

    default:
      return state;
  }
};
