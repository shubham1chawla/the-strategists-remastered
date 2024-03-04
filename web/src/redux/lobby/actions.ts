import { Player, Land } from '.';

export namespace LobbyActions {
  export const Types = {
    SET_STATE: 'SET_STATE',
    SET_PLAYERS_COUNT_CONSTRAINTS: 'SET_PLAYERS_COUNT_CONSTRAINTS',
    SET_PLAYERS: 'SET_PLAYERS',
    ADD_PLAYER: 'ADD_PLAYER',
    KICK_PLAYER: 'KICK_PLAYER',
    PATCH_PLAYERS: 'PATCH_PLAYERS',
    SET_LANDS: 'SET_LANDS',
    PATCH_LANDS: 'PATCH_LANDS',
  };

  export const setState = (state: 'LOBBY' | 'ACTIVE') => {
    return {
      type: Types.SET_STATE,
      payload: state,
    };
  };

  export const setPlayersCountConstraints = (
    minPlayersCount: number,
    maxPlayersCount: number
  ) => {
    return {
      type: Types.SET_PLAYERS_COUNT_CONSTRAINTS,
      payload: [minPlayersCount, maxPlayersCount],
    };
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

  export const kickPlayer = (id: number) => {
    return {
      type: Types.KICK_PLAYER,
      payload: id,
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

  export const patchLands = (lands: Land[]) => {
    return {
      type: Types.PATCH_LANDS,
      payload: lands,
    };
  };
}
