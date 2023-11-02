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

export const getAllActivityTypes = (): ActivityType[] => {
  return [
    'BANKRUPTCY',
    'BONUS',
    'CHEAT',
    'END',
    'EVENT',
    'INVEST',
    'JAIL',
    'JOIN',
    'KICK',
    'MOVE',
    'RENT',
    'RESET',
    'START',
    'TRADE',
    'TURN',
  ];
};

export interface Activity {
  type: ActivityType;
  val1: string;
  val2: string | null;
  val3: string | null;
  val4: string | null;
  val5: string | null;
}

export interface ActivityState {
  subscribedTypes: ActivityType[];
  activities: Activity[];
}

const initialState: ActivityState = {
  subscribedTypes: [...getAllActivityTypes()],
  activities: [],
};

export const activityReducer = (
  state: ActivityState = initialState,
  action: any
): ActivityState => {
  const { type, payload } = action;
  switch (type) {
    case ActivityActions.Types.SET_ACTIVITIES:
      return {
        ...state,
        activities: [...payload],
      };

    case ActivityActions.Types.ADD_ACTIVITY:
      return {
        ...state,
        activities: [payload, ...state.activities],
      };

    case ActivityActions.Types.SET_SUBSCRIBED_TYPES:
      return {
        ...state,
        subscribedTypes: [...payload],
      };

    default:
      return state;
  }
};
