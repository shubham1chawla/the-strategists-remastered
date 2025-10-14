import { createSlice } from '@reduxjs/toolkit';

export interface PlayerTrend {
  playerId: number;
  cash: number;
  netWorth: number;
  step: number;
}

export interface LandTrend {
  landId: number;
  marketValue: number;
  step: number;
}

export type Trend = Partial<PlayerTrend> & Partial<LandTrend>;

export type TrendsState = Trend[];

const initialState: TrendsState = [];

const slice = createSlice({
  name: 'trends',
  initialState,
  reducers: {
    trendsSetted: (_, { payload }: { payload: Trend[] }) => [...payload],
    trendsAdded: (state, { payload }: { payload: Trend[] }) => [
      ...state,
      ...payload,
    ],
  },
});

export const { trendsSetted, trendsAdded } = slice.actions;

export default slice.reducer;
