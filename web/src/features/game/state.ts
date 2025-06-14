import { createSlice } from '@reduxjs/toolkit';

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

export interface GameState {
  players: Player[];
  lands: Land[];
  state: 'LOBBY' | 'ACTIVE';
  minPlayersCount: number;
  maxPlayersCount: number;
}

const initialState: GameState = {
  players: [],
  lands: [],
  state: 'LOBBY',
  minPlayersCount: 0,
  maxPlayersCount: 0,
};

const slice = createSlice({
  name: 'game',
  initialState,
  reducers: {
    gameStateSetted: (state, { payload }: { payload: GameState['state'] }) => {
      state.state = payload;
    },
    playersCountConstraintsSetted: (
      state,
      { payload }: { payload: [number, number] }
    ) => {
      state.minPlayersCount = payload[0];
      state.maxPlayersCount = payload[1];
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
      const patches = new Map<number, Player>();
      for (const p of payload || []) {
        patches.set(p.id, p);
      }
      state.players = state.players.map(
        (player) => patches.get(player.id) || player
      );
    },
    landsSetted: (state, { payload }: { payload: Land[] }) => {
      state.lands = [...payload];
    },
    landsPatched: (state, { payload }: { payload: Land[] }) => {
      const patches = new Map<number, Land>();
      for (const land of payload || []) {
        patches.set(land.id, land);
      }
      state.lands = state.lands.map((land) => patches.get(land.id) || land);
    },
  },
});

export const {
  gameStateSetted,
  playersCountConstraintsSetted,
  playersSetted,
  playerAdded,
  playerKicked,
  playersPatched,
  landsSetted,
  landsPatched,
} = slice.actions;

export default slice.reducer;
