import { Player, Land } from '.';

export namespace LobbyActions {
  export const Types = {
    SET_PLAYERS: 'SET_PLAYERS',
    ADD_PLAYER: 'ADD_PLAYER',
    KICK_PLAYER: 'KICK_PLAYER',
    SET_LANDS: 'SET_LANDS',
  };

  export const setPlayers = (players: Player[]) => {
    return {
      type: Types.SET_PLAYERS,
      payload: players,
    };
  };

  export const addPlayer = (player: Player) => {
    return {
      type: Types.ADD_PLAYER,
      payload: player,
    };
  };

  export const kickPlayer = (username: string) => {
    return {
      type: Types.KICK_PLAYER,
      payload: username,
    };
  };

  export const setLands = (lands: Land[]) => {
    return {
      type: Types.SET_LANDS,
      payload: lands,
    };
  };
}
