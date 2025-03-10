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
  state: 'ACTIVE' | 'BANKRUPT';
  turn: boolean;
  host: boolean;
  netWorth: number;
  cash: number;
  bankruptcyOrder: number;
  lands: PlayerLand[];

  // Optional fields based on configuration
  remainingSkipsCount?: number;
  allowedSkipsCount?: number;
}

export interface Land {
  id: number;
  name: string;
  x: number;
  y: number;
  playerPosition: 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right';
  marketValue: number;
  totalOwnership: number;
  players: PlayerLand[];
  events: any[];
}

export interface LobbyState {
  players: Player[];
  lands: Land[];
  state: 'LOBBY' | 'ACTIVE';
  minPlayersCount: number;
  maxPlayersCount: number;
}

const initialState: LobbyState = {
  players: [],
  lands: [],
  state: 'LOBBY',
  minPlayersCount: 0,
  maxPlayersCount: 0,
};

export const lobbyReducer = (
  state: LobbyState = initialState,
  action: any
): LobbyState => {
  const { type, payload } = action;
  switch (type) {
    case LobbyActions.Types.SET_STATE:
      return {
        ...state,
        state: payload,
      };

    case LobbyActions.Types.SET_PLAYERS_COUNT_CONSTRAINTS:
      return {
        ...state,
        minPlayersCount: payload[0],
        maxPlayersCount: payload[1],
      };

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
        players: state.players.filter((player) => player.id !== payload),
      };

    case LobbyActions.Types.PATCH_PLAYERS: {
      const patches = new Map<number, Player>();
      for (const p of (payload as Player[]) || []) {
        patches.set(p.id, p);
      }
      return {
        ...state,
        players: state.players.map((p) => patches.get(p.id) || p),
      };
    }

    case LobbyActions.Types.SET_LANDS:
      return {
        ...state,
        lands: [...payload],
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
