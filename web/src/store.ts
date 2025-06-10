import { configureStore } from '@reduxjs/toolkit';
import activitiesReducer, {
  ActivitiesState,
} from './features/activities/slice';
import advicesReducer, { AdvicesState } from './features/advices/slice';
import gameReducer, { GameState } from './features/game/slice';
import loginReducer, { LoginState } from './features/login/slice';
import predictionsReducer, {
  PredictionsState,
} from './features/predictions/slice';
import trendsReducer, { TrendsState } from './features/trends/slice';
import logger from 'redux-logger';

export interface State {
  activities: ActivitiesState;
  advices: AdvicesState;
  game: GameState;
  login: LoginState;
  predictions: PredictionsState;
  trends: TrendsState;
}

export const store = configureStore({
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
