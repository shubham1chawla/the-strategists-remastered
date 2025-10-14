import { createSlice } from '@reduxjs/toolkit';

export interface PlayerPrediction {
  playerId: number;
  winnerProbability: number;
  bankruptProbability: number;
  prediction: 'WINNER' | 'BANKRUPT';
  step: number;
}

export type PredictionsState = PlayerPrediction[];

const initialState: PredictionsState = [];

const slice = createSlice({
  name: 'predictions',
  initialState,
  reducers: {
    playerPredictionsSetted: (
      _,
      { payload }: { payload: PlayerPrediction[] },
    ) => [...payload],
    playerPredictionsAdded: (
      state,
      { payload }: { payload: PlayerPrediction[] },
    ) => [...state, ...payload],
  },
});

export const { playerPredictionsSetted, playerPredictionsAdded } =
  slice.actions;

export default slice.reducer;
