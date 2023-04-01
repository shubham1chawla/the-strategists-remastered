import { ADD_PLAYER } from './lobbyTypes';
import { KICK_PLAYER } from './lobbyTypes';

export interface Player {
  username: string;
  cash: number;
}

interface LobbyState {
  players: Player[];
}

const initialState: LobbyState = {
  players: [],
};

const lobbyReducer = (state: LobbyState = initialState, action: any) => {
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
          (player) => player.username !== action.payload.username
        ),
      };

    default:
      return state;
  }
};

export default lobbyReducer;
