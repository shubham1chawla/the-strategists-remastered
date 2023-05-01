import { LobbyActions } from '.';

export interface Player {
  id: number;
  username: string;
  index: number;
  state: string;
  turn: boolean;
  remainingJailLife: number;
  netWorth: number;
  cash: number;
}

export interface Land {
  id: number;
  name: string;
  x: number;
  y: number;
  marketValue: number;
  totalOwnership: number;
  players: Player[];
  events: any[];
}

export interface LobbyState {
  players: Player[];
  lands: Land[];
}

const initialState: LobbyState = {
  players: [],
  lands: [],
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

    default:
      return state;
  }
};
