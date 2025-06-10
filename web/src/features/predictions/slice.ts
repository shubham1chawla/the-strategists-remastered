import { createSlice } from '@reduxjs/toolkit';

export interface Prediction {
  playerId: number;
  winnerProbability: number;
  bankruptProbability: number;
  type: 'WINNER' | 'BANKRUPT';
  turn: number;
}

export type PredictionsState = Prediction[];

const initialState: PredictionsState = [];

const slice = createSlice({
  name: 'predictions',
  initialState,
  reducers: {
    predictionsSetted: (_, { payload }: { payload: Prediction[] }) => [
      ...payload,
    ],
    predictionsAdded: (state, { payload }: { payload: Prediction[] }) => [
      ...state,
      ...payload,
    ],
  },
});

export const { predictionsSetted, predictionsAdded } = slice.actions;

export default slice.reducer;
