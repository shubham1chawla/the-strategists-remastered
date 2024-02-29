import { TrendActions } from './actions';

export interface PlayerTrend {
  playerId: number;
  cash: number;
  netWorth: number;
}

export interface LandTrend {
  landId: number;
  marketValue: number;
}

export type Trend = Partial<PlayerTrend> & Partial<LandTrend>;

export type TrendState = Trend[];

export const trendReducer = (
  state: TrendState = [],
  action: any
): TrendState => {
  const { type, payload } = action;
  switch (type) {
    case TrendActions.Types.SET_TRENDS:
      return [...payload];
    case TrendActions.Types.ADD_TRENDS:
      return [...state, ...payload];
    default:
      return state;
  }
};
