import { UserActions } from '.';

export interface UserState {
  type: 'player' | 'admin';
  username?: string;
}

export const userReducer = (state = initialState(), action: any): UserState => {
  const { type, payload } = action;
  switch (type) {
    case UserActions.Types.SET_USER:
      localStorage.setItem('user', JSON.stringify(payload));
      return {
        ...state,
        ...payload,
      };

    case UserActions.Types.UNSET_USER:
      localStorage.removeItem('user');
      return initialState();

    default:
      return state;
  }
};

const initialState = (): UserState => {
  const json = localStorage.getItem('user');
  return json ? JSON.parse(json) : { type: 'player' };
};
