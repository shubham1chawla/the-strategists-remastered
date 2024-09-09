import { configureStore } from '@reduxjs/toolkit';
import { ActivityState, activityReducer } from './activity';
import { adviceReducer, AdviceState } from './advices';
import { LobbyState, lobbyReducer } from './lobby';
import { LoginState, loginReducer } from './login';
import { PredictionState, predictionReducer } from './predictions';
import { TrendState, trendReducer } from './trends';
import logger from 'redux-logger';

export interface State {
  activity: ActivityState;
  advice: AdviceState;
  lobby: LobbyState;
  login: LoginState;
  prediction: PredictionState;
  trend: TrendState;
}

export const store = configureStore({
  reducer: {
    activity: activityReducer,
    advice: adviceReducer,
    lobby: lobbyReducer,
    login: loginReducer,
    prediction: predictionReducer,
    trend: trendReducer,
  },
  devTools: process.env.NODE_ENV !== 'production',
  middleware: (defaultMiddlewares) => {
    if (process.env.NODE_ENV !== 'production') {
      // For some reason, logger needs to be typecasted to any to avoid errors.
      return defaultMiddlewares().concat(logger as any);
    }
    return defaultMiddlewares();
  },
});
