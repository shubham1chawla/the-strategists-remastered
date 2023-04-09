import { SET_USER } from '.';

export interface UserState {
  type: 'player' | 'admin';
  username: string;
}

const initialState: UserState = {
  type: 'player',
  username: 'Unknown',
};

export const userReducer = (state = initialState, action: any): UserState => {
  switch (action.type) {
    case SET_USER:
      return {
        ...state,
        ...action.payload,
      };
    default:
      return state;
  }
};
