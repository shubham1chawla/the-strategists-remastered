import { ADD_PLAYER, KICK_PLAYER } from '.';

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
  switch (action.type) {
    case ADD_PLAYER:
      return {
        ...state,
        players: [...state.players, action.payload],
      };

    case KICK_PLAYER:
      return {
        ...state,
        players: state.players.filter(
          (player) => player.username !== action.payload
        ),
      };

    default:
      return state;
  }
};
