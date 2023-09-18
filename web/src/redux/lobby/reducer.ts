import { LobbyActions } from '.';

export interface PlayerLand {
  landId?: number;
  playerId?: number;
  ownership: number;
  buyAmount: number;
}

export interface Player {
  id: number;
  username: string;
  index: number;
  state: 'ACTIVE' | 'BANKRUPT' | 'JAIL';
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
  state: 'LOBBY' | 'ACTIVE';
}

const initialState: LobbyState = {
  players: [],
  lands: [],
  state: 'LOBBY',
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

    case LobbyActions.Types.PATCH_PLAYERS: {
      const patches = new Map<number, Player>();
      for (const player of (payload as Player[]) || []) {
        patches.set(player.id, player);
      }
      return {
        ...state,
        players: state.players.map((player) => {
          const patch = patches.get(player.id);
          return patch ? patch : player;
        }),
      };
    }

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

    case LobbyActions.Types.PATCH_LANDS: {
      const patches = new Map<number, Land>();
      for (const land of (payload as Land[]) || []) {
        patches.set(land.id, land);
      }
      return {
        ...state,
        lands: state.lands.map((land) => {
          const patch = patches.get(land.id);
          return patch ? patch : land;
        }),
      };
    }

    default:
      return state;
  }
};
