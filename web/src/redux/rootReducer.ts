import { combineReducers } from 'redux';
import gameReducer from './game/gameReducer';

const rootReducer = combineReducers({
  game: gameReducer,
});

export default rootReducer;
