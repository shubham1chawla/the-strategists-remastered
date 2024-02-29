import { Trend } from './reducer';

export namespace TrendActions {
  export const Types = {
    SET_TRENDS: 'SET_TRENDS',
    ADD_TRENDS: 'ADD_TRENDS',
  };

  export const setTrends = (trends: Trend[]) => {
    return {
      type: Types.SET_TRENDS,
      payload: trends,
    };
  };

  export const addTrends = (trends: Trend[]) => {
    return {
      type: Types.ADD_TRENDS,
      payload: trends,
    };
  };
}
