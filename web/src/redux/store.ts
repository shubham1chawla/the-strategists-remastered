import { createStore, applyMiddleware, combineReducers } from 'redux';
import { composeWithDevTools } from 'redux-devtools-extension';
import { LobbyState, lobbyReducer } from './lobby';
import { UserState, userReducer } from './user';
import { ActivityState, activityReducer } from './activity';
import logger from 'redux-logger';

export interface State {
  activity: ActivityState;
  lobby: LobbyState;
  user: UserState;
}

export const store = createStore(
  combineReducers({
    activity: activityReducer,
    lobby: lobbyReducer,
    user: userReducer,
  }),
  composeWithDevTools(applyMiddleware(logger))
);
