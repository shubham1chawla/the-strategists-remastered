import { createStore, applyMiddleware, combineReducers } from 'redux';
import { composeWithDevTools } from 'redux-devtools-extension';
import { lobbyReducer } from './lobby';
import { userReducer } from './user';
import { activityReducer } from './activity';
import logger from 'redux-logger';

export const store = createStore(
  combineReducers({
    activities: activityReducer,
    lobby: lobbyReducer,
    user: userReducer,
  }),
  composeWithDevTools(applyMiddleware(logger))
);
