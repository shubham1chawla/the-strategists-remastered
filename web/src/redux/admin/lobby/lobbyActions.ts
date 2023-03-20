import { ADD_PLAYER } from './lobbyTypes';
import { REMOVE_PLAYER } from './lobbyTypes';
import { Player } from './lobbyReducer';

export const addPlayer = (player: Player) => {
  return {
    type: ADD_PLAYER,
    payload: player,
  };
};

export const removePlayer = (player: Player) => {
  return {
    type: REMOVE_PLAYER,
    payload: player,
  };
};
