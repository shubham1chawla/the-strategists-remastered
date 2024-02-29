import { configureStore } from '@reduxjs/toolkit';
import { ActivityState, activityReducer } from './activity';
import { LobbyState, lobbyReducer } from './lobby';
import { TrendState, trendReducer } from './trends';
import { LoginState, loginReducer } from './login';
import logger from 'redux-logger';

export interface State {
  activity: ActivityState;
  lobby: LobbyState;
  trend: TrendState;
  login: LoginState;
}

export const store = configureStore({
  reducer: {
    activity: activityReducer,
    lobby: lobbyReducer,
    trend: trendReducer,
    login: loginReducer,
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
