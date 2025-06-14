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
  middleware: (defaultMiddlewares) => {
    if (process.env.NODE_ENV !== 'production') {
      // For some reason, logger needs to be typecasted to any to avoid errors.
      return defaultMiddlewares().concat(logger as any);
    }
    return defaultMiddlewares();
  },
});

export default store;
