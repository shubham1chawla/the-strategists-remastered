import { ADD_PLAYER, KICK_PLAYER, Player } from '.';

export const addPlayer = (player: Player) => {
  return {
    type: ADD_PLAYER,
    payload: player,
  };
};

export const kickPlayer = (username: string) => {
  return {
    type: KICK_PLAYER,
    payload: username,
  };
};
