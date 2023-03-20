import { ADD_PLAYER } from './lobbyTypes';
import { REMOVE_PLAYER } from './lobbyTypes';

export interface Player {
  name: string;
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

    case REMOVE_PLAYER:
      return {
        ...state,
        players: state.players.filter(
          (player) => player.name !== action.payload.name
        ),
      };

    default:
      return state;
  }
};

export default lobbyReducer;
