import { configureStore } from '@reduxjs/toolkit';
import { ActivityState, activityReducer } from './activity';
import { LobbyState, lobbyReducer } from './lobby';
import { TrendState, trendReducer } from './trend';
import { UserState, userReducer } from './user';
import logger from 'redux-logger';

export interface State {
  activity: ActivityState;
  lobby: LobbyState;
  trend: TrendState;
  user: UserState;
}

export const store = configureStore({
  reducer: {
    activity: activityReducer,
    lobby: lobbyReducer,
    trend: trendReducer,
    user: userReducer,
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
