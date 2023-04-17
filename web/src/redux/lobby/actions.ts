import { Player } from '.';

export namespace LobbyActions {
  export const Types = {
    ADD_PLAYER: 'ADD_PLAYER',
    KICK_PLAYER: 'KICK_PLAYER',
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
}
