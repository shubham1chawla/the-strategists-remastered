import { ActivityActions } from '.';

export type ActivityType =
  | 'BANKRUPTCY'
  | 'BONUS'
  | 'CHEAT'
  | 'END'
  | 'EVENT'
  | 'INVEST'
  | 'JAIL'
  | 'JOIN'
  | 'KICK'
  | 'MOVE'
  | 'RENT'
  | 'RESET'
  | 'START'
  | 'TRADE'
  | 'TURN';

export interface Activity {
  type: ActivityType;
  val1: string;
  val2: string | null;
  val3: string | null;
  val4: string | null;
  val5: string | null;
}

export type ActivityState = Activity[];

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
