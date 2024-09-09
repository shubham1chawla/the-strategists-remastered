import { AdviceActions } from './actions';

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
  val1: string | null;
  val2: string | null;
  val3: string | null;
}

export type AdviceState = Advice[];

export const adviceReducer = (state: AdviceState = [], action: any) => {
  const { type, payload } = action;
  switch (type) {
    case AdviceActions.Types.SET_ADVICES:
      return [...payload];
    case AdviceActions.Types.ADD_OR_PATCH_ADVICES: {
      const toMap = (advices: Advice[]) =>
        advices.reduce((map, advice) => {
          map.set(advice.id, advice);
          return map;
        }, new Map<number, Advice>());

      const oldAdvices = toMap(state);
      const newAdvices = toMap((payload as Advice[]) || []);
      return [
        ...state.map((advice) => newAdvices.get(advice.id) || advice),
        ...((payload as Advice[]) || []).filter(
          (advice) => !oldAdvices.has(advice.id)
        ),
      ];
    }
    default:
      return state;
  }
};
