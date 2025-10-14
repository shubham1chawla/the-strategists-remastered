import { createSlice } from '@reduxjs/toolkit';

export type UpdateType =
  | 'ADVICE'
  | 'BANKRUPTCY'
  | 'BONUS'
  | 'CHEAT'
  | 'CLEAN_UP'
  | 'CREATE'
  | 'EVENT'
  | 'INVEST'
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
    'CREATE',
    'EVENT',
    'INVEST',
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
  step: number;
  text: string;
}

export interface ActivitiesState {
  subscribedTypes: UpdateType[];
  activities: Activity[];
}

const initialState: ActivitiesState = {
  subscribedTypes: [...getSubscribableTypes()],
  activities: [],
};

const slice = createSlice({
  name: 'activities',
  initialState,
  reducers: {
    activitiesSetted: (state, { payload }: { payload: Activity[] }) => {
      state.activities = [...payload];
    },
    activityAdded: (state, { payload }: { payload: Activity }) => {
      state.activities = [payload, ...state.activities];
    },
    subscribedTypesSetted: (state, { payload }: { payload: UpdateType[] }) => {
      state.subscribedTypes = [...payload];
    },
  },
});

export const { activitiesSetted, activityAdded, subscribedTypesSetted } =
  slice.actions;

export default slice.reducer;
