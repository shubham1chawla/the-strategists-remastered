import { Player, Land } from '.';

export namespace LobbyActions {
  export const Types = {
    SET_PLAYERS: 'SET_PLAYERS',
    ADD_PLAYER: 'ADD_PLAYER',
    KICK_PLAYER: 'KICK_PLAYER',
    PATCH_PLAYERS: 'PATCH_PLAYERS',
    SET_LANDS: 'SET_LANDS',
    SET_STATE: 'SET_STATE',
    PATCH_LANDS: 'PATCH_LANDS',
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

  export const patchPlayers = (players: Player[]) => {
    return {
      type: Types.PATCH_PLAYERS,
      payload: players,
    };
  };

  export const setLands = (lands: Land[]) => {
    return {
      type: Types.SET_LANDS,
      payload: lands,
    };
  };

  export const setState = (state: 'LOBBY' | 'ACTIVE') => {
    return {
      type: Types.SET_STATE,
      payload: state,
    };
  };

  export const patchLands = (lands: Land[]) => {
    return {
      type: Types.PATCH_LANDS,
      payload: lands,
    };
  };
}
