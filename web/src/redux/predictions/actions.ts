import { Prediction } from './reducer';

export namespace PredictionActions {
  export const Types = {
    SET_PREDICTIONS: 'SET_PREDICTIONS',
    ADD_PREDICTIONS: 'ADD_PREDICTIONS',
  };

  export const setPredictions = (predictions: Prediction[]) => {
    return {
      type: Types.SET_PREDICTIONS,
      payload: predictions,
    };
  };

  export const addPredictions = (predictions: Prediction[]) => {
    return {
      type: Types.ADD_PREDICTIONS,
      payload: predictions,
    };
  };
}
