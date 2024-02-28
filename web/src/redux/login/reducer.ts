import { LoginActions } from '.';

const SESSION_STORAGE_KEY = 'login';

export interface LoginState {
  gameCode?: string;
  playerId?: number;
}

export const loginReducer = (
  state = initialState(),
  action: any
): LoginState => {
  const { type, payload } = action;
  switch (type) {
    case LoginActions.Types.LOGIN:
      sessionStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(payload));
      return {
        ...state,
        ...payload,
      };

    case LoginActions.Types.LOGOUT:
      sessionStorage.removeItem(SESSION_STORAGE_KEY);
      return initialState();

    default:
      return state;
  }
};

const initialState = (): LoginState => {
  const json = sessionStorage.getItem(SESSION_STORAGE_KEY);
  return json ? JSON.parse(json) : {};
};
