import { createStore, applyMiddleware, combineReducers } from 'redux';
import { composeWithDevTools } from 'redux-devtools-extension';
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

export const store = createStore(
  combineReducers({
    activity: activityReducer,
    lobby: lobbyReducer,
    trend: trendReducer,
    user: userReducer,
  }),
  composeWithDevTools(applyMiddleware(logger))
);
