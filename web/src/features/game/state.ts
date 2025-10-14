import { createSlice } from '@reduxjs/toolkit';

export interface PlayerLand {
  landId?: number;
  playerId?: number;
  ownership: number;
  buyAmount: number;
}

export interface Rent {
  id: number;
  step: number;
  rentAmount: number;
  sourcePlayerId?: number;
  targetPlayerId?: number;
  landId: number;
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
  receivedRents: Rent[] | null;
  paidRents: Rent[] | null;

  // Optional fields based on configuration
  lastInvestStep?: number;
  lastSkippedStep?: number;
  remainingSkipsCount?: number;
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

export interface Game {
  code: string;
  state: 'LOBBY' | 'ACTIVE';
  currentStep: number;
  minPlayersCount: number;
  maxPlayersCount: number;
  gameMapId: string;
  diceSize: number;
  createdAt: number;
  endAt?: number;

  // Optional fields based on configuration
  allowedSkipsCount?: number;
  skipPlayerTimeout?: number;
  cleanUpDelay?: number;
}

export interface GameState {
  game: Game;
  players: Player[];
  lands: Land[];
}

const initialState: GameState = {
  game: {
    code: '',
    state: 'LOBBY',
    currentStep: 0,
    minPlayersCount: 0,
    maxPlayersCount: 0,
    gameMapId: '',
    diceSize: 0,
    createdAt: 0,
  },
  players: [],
  lands: [],
};

const slice = createSlice({
  name: 'game',
  initialState,
  reducers: {
    gameSetted: (state, { payload }: { payload: Game }) => {
      state.game = { ...payload };
    },
    gamePatched: (state, { payload }: { payload: Partial<Game> }) => {
      state.game = { ...state.game, ...payload };
    },
    playersSetted: (state, { payload }: { payload: Player[] }) => {
      state.players = [...payload];
    },
    playerAdded: (state, { payload }: { payload: Player }) => {
      state.players = [...state.players, payload];
    },
    playerKicked: (state, { payload }: { payload: number }) => {
      state.players = state.players.filter(({ id }) => id !== payload);
    },
    playersPatched: (state, { payload }: { payload: Player[] }) => {
      const patches = (payload || []).reduce((map, player) => {
        map.set(player.id, player);
        return map;
      }, new Map<number, Player>());
      state.players = state.players.map(
        (player) => patches.get(player.id) || player,
      );
    },
    landsSetted: (state, { payload }: { payload: Land[] }) => {
      state.lands = [...payload];
    },
    landsPatched: (state, { payload }: { payload: Land[] }) => {
      const patches = (payload || []).reduce((map, land) => {
        map.set(land.id, land);
        return map;
      }, new Map<number, Land>());
      state.lands = state.lands.map((land) => patches.get(land.id) || land);
    },
  },
});

export const {
  gameSetted,
  gamePatched,
  playersSetted,
  playerAdded,
  playerKicked,
  playersPatched,
  landsSetted,
  landsPatched,
} = slice.actions;

export default slice.reducer;
