import { createSlice } from '@reduxjs/toolkit';

export type AdviceType =
  | 'FREQUENTLY_INVEST'
  | 'CONCENTRATE_INVESTMENTS'
  | 'AVOID_TIMEOUT'
  | 'SIGNIFICANT_INVESTMENTS'
  | 'POTENTIAL_BANKRUPTCY';

export interface Advice {
  id: number;
  playerId: number;
  state: 'NEW' | 'FOLLOWED';
  priority: number;
  type: AdviceType;
  viewed: boolean;
  text: string;
}

export type AdvicesState = Advice[];

const initialState: AdvicesState = [];

const slice = createSlice({
  name: 'advices',
  initialState,
  reducers: {
    advicesSetted: (_, { payload }: { payload: Advice[] }) => [...payload],
    advicesAddedOrPatched: (state, { payload }: { payload: Advice[] }) => {
      const toMap = (advices: Advice[]) =>
        advices.reduce((map, advice) => {
          map.set(advice.id, advice);
          return map;
        }, new Map<number, Advice>());

      const oldAdvices = toMap(state);
      const newAdvices = toMap(payload || []);
      return [
        ...state.map((advice) => newAdvices.get(advice.id) || advice),
        ...(payload || []).filter((advice) => !oldAdvices.has(advice.id)),
      ];
    },
  },
});

export const { advicesSetted, advicesAddedOrPatched } = slice.actions;

export default slice.reducer;
