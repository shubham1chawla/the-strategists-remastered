import { PredictionActions } from './actions';

export interface Prediction {
  playerId: number;
  winnerProbability: number;
  bankruptProbability: number;
  type: 'WINNER' | 'BANKRUPT';
  turn: number;
}

export type PredictionState = Prediction[];

export const predictionReducer = (
  state: PredictionState = [],
  action: any
): PredictionState => {
  const { type, payload } = action;
  switch (type) {
    case PredictionActions.Types.SET_PREDICTIONS:
      return [...payload];
    case PredictionActions.Types.ADD_PREDICTIONS:
      return [...state, ...payload];
    default:
      return state;
  }
};
