import { configureStore } from '@reduxjs/toolkit';
import logger from 'redux-logger';
import activitiesStateReducer, { ActivitiesState } from '@activities/state';
import advicesStateReducer, { AdvicesState } from '@advices/state';
import gameStateReducer, { GameState } from '@game/state';
import loginStateReducer, { LoginState } from '@login/state';
import predictionsStateReducer, { PredictionsState } from '@predictions/state';
import trendsStateReducer, { TrendsState } from '@trends/state';

export interface StrategistsState {
  activitiesState: ActivitiesState;
  advicesState: AdvicesState;
  gameState: GameState;
  loginState: LoginState;
  predictionsState: PredictionsState;
  trendsState: TrendsState;
}

const store = configureStore({
  reducer: {
    activitiesState: activitiesStateReducer,
    advicesState: advicesStateReducer,
    gameState: gameStateReducer,
    loginState: loginStateReducer,
    predictionsState: predictionsStateReducer,
    trendsState: trendsStateReducer,
  },
  devTools: process.env.NODE_ENV !== 'production',
  middleware: (defaultMiddlewares) =>
    defaultMiddlewares().concat(
      process.env.NODE_ENV !== 'production' ? [logger] : [],
    ),
});

export default store;
