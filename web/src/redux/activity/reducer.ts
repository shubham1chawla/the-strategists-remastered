import { ActivityActions } from '.';

export type UpdateType =
  | 'BANKRUPTCY'
  | 'BONUS'
  | 'CHEAT'
  | 'EVENT'
  | 'INVEST'
  | 'INVITE'
  | 'JOIN'
  | 'KICK'
  | 'MOVE'
  | 'PING'
  | 'PREDICTION'
  | 'RENT'
  | 'RESET'
  | 'SKIP'
  | 'START'
  | 'TRADE'
  | 'TREND'
  | 'TURN'
  | 'WIN';

export const getSubscribableTypes = (): UpdateType[] => {
  return [
    'BANKRUPTCY',
    'BONUS',
    'CHEAT',
    'EVENT',
    'INVEST',
    'INVITE',
    'JOIN',
    'KICK',
    'MOVE',
    'PREDICTION',
    'RENT',
    'RESET',
    'SKIP',
    'START',
    'TRADE',
    'TURN',
    'WIN',
  ];
};

export interface Activity {
  type: UpdateType;
  val1: string;
  val2: string | null;
  val3: string | null;
  val4: string | null;
  val5: string | null;
}

export interface ActivityState {
  subscribedTypes: UpdateType[];
  activities: Activity[];
}

const initialState: ActivityState = {
  subscribedTypes: [...getSubscribableTypes()],
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
