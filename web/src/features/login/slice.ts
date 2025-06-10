import { createSlice } from '@reduxjs/toolkit';

const SESSION_STORAGE_KEY = 'login';

export interface LoginState {
  gameCode?: string;
  playerId?: number;
}

const initialState = (): LoginState => {
  const json = sessionStorage.getItem(SESSION_STORAGE_KEY);
  return json ? JSON.parse(json) : {};
};

const slice = createSlice({
  name: 'login',
  initialState: initialState(),
  reducers: {
    loggedIn: (state, { payload }: { payload: LoginState }) => {
      sessionStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(payload));
      return {
        ...state,
        ...payload,
      };
    },
    loggedOut: () => {
      sessionStorage.removeItem(SESSION_STORAGE_KEY);
      return initialState();
    },
  },
});

export const { loggedIn, loggedOut } = slice.actions;

export default slice.reducer;
