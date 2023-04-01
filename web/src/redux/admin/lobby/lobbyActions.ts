import { ADD_PLAYER } from './lobbyTypes';
import { KICK_PLAYER } from './lobbyTypes';
import { Player } from './lobbyReducer';

export const addPlayer = (player: Player) => {
  return {
    type: ADD_PLAYER,
    payload: player,
  };
};

export const kickPlayer = (player: Player) => {
  return {
    type: KICK_PLAYER,
    payload: player,
  };
};
