import { UserActions } from '.';

export interface UserState {
  type: 'player' | 'admin';
  username: string;
}

const initialState: UserState = {
  type: 'player',
  username: 'Unknown',
};

export const userReducer = (state = initialState, action: any): UserState => {
  const { type, payload } = action;
  switch (type) {
    case UserActions.Types.SET_USER:
      return {
        ...state,
        ...payload,
      };

    default:
      return state;
  }
};
