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

interface LobbyState {
  players: Player[];
}

const initialState: LobbyState = {
  players: [],
};

export const lobbyReducer = (
  state: LobbyState = initialState,
  action: any
): LobbyState => {
  const { type, payload } = action;
  switch (type) {
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

    default:
      return state;
  }
};
