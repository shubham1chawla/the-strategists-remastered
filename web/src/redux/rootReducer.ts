import { combineReducers } from 'redux';
import { lobbyReducer } from './lobby';
import { userReducer } from './user';

export const rootReducer = combineReducers({
  lobby: lobbyReducer,
  user: userReducer,
});
