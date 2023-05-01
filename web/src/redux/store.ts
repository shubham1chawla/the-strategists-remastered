import { createStore, applyMiddleware, combineReducers } from 'redux';
import { composeWithDevTools } from 'redux-devtools-extension';
import { LobbyState, lobbyReducer } from './lobby';
import { UserState, userReducer } from './user';
import { ActivityState, activityReducer } from './activity';
import logger from 'redux-logger';

export interface State {
  activities: ActivityState;
  lobby: LobbyState;
  user: UserState;
}

export const store = createStore(
  combineReducers({
    activities: activityReducer,
    lobby: lobbyReducer,
    user: userReducer,
  }),
  composeWithDevTools(applyMiddleware(logger))
);
