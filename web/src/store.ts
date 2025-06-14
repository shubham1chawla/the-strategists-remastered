import { configureStore } from '@reduxjs/toolkit';
import activitiesReducer, { ActivitiesState } from '@activities/state';
import advicesReducer, { AdvicesState } from '@advices/state';
import gameReducer, { GameState } from '@game/state';
import loginReducer, { LoginState } from '@login/state';
import predictionsReducer, { PredictionsState } from '@predictions/state';
import trendsReducer, { TrendsState } from '@trends/state';
import logger from 'redux-logger';

export interface State {
  activities: ActivitiesState;
  advices: AdvicesState;
  game: GameState;
  login: LoginState;
  predictions: PredictionsState;
  trends: TrendsState;
}

const store = configureStore({
  reducer: {
    activities: activitiesReducer,
    advices: advicesReducer,
    game: gameReducer,
    login: loginReducer,
    predictions: predictionsReducer,
    trends: trendsReducer,
  },
  devTools: process.env.NODE_ENV !== 'production',
  middleware: (defaultMiddlewares) =>
    defaultMiddlewares().concat(
      process.env.NODE_ENV !== 'production' ? [logger] : [],
    ),
});

export default store;
