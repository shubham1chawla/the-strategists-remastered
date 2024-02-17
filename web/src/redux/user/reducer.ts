import { UserActions } from '.';

export interface UserState {
  type: 'ADMIN' | 'PLAYER';
  username?: string;
  gameId?: number;
}

export const userReducer = (state = initialState(), action: any): UserState => {
  const { type, payload } = action;
  switch (type) {
    case UserActions.Types.SET_USER:
      sessionStorage.setItem('user', JSON.stringify(payload));
      return {
        ...state,
        ...payload,
      };

    case UserActions.Types.UNSET_USER:
      sessionStorage.removeItem('user');
      return initialState();

    default:
      return state;
  }
};

const initialState = (): UserState => {
  const json = sessionStorage.getItem('user');
  return json ? JSON.parse(json) : { type: 'PLAYER' };
};
