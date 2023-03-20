import { combineReducers } from 'redux';
import gameReducer from './game/gameReducer';
import lobbyReducer from './admin/lobby/lobbyReducer';

const rootReducer = combineReducers({
  game: gameReducer,
  lobby: lobbyReducer,
});

export default rootReducer;
