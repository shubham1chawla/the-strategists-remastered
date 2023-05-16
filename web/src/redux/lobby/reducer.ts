import { LobbyActions } from '.';

export interface PlayerLand {
  landId?: number;
  playerId?: number;
  ownership: number;
  buyAmount: number;

  // fields for frontend state linking only
  // these are not part of backend entities
  land?: Land;
  player?: Player;
}

export interface Player {
  id: number;
  username: string;
  index: number;
  state: string;
  turn: boolean;
  remainingJailLife: number;
  netWorth: number;
  cash: number;
  lands: PlayerLand[];
}

export interface Land {
  id: number;
  name: string;
  x: number;
  y: number;
  marketValue: number;
  totalOwnership: number;
  players: PlayerLand[];
  events: any[];
}

export interface LobbyState {
  players: Player[];
  lands: Land[];
  state: 'lobby' | 'active';
}

const initialState: LobbyState = {
  players: [],
  lands: [],
  state: 'lobby',
};

export const lobbyReducer = (
  state: LobbyState = initialState,
  action: any
): LobbyState => {
  const { type, payload } = action;
  switch (type) {
    case LobbyActions.Types.SET_PLAYERS:
      return {
        ...state,
        players: [...payload],
      };

    case LobbyActions.Types.ADD_PLAYER:
      return {
        ...state,
        players: [...state.players, payload],
      };

    case LobbyActions.Types.KICK_PLAYER:
      return {
        ...state,
        players: state.players.filter((player) => player.username !== payload),
      };

    case LobbyActions.Types.SET_LANDS:
      return {
        ...state,
        lands: [...payload],
      };

    case LobbyActions.Types.SET_STATE:
      return {
        ...state,
        state: payload,
      };

    default:
      return state;
  }
};
