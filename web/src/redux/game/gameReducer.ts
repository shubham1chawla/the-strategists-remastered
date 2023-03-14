import { SET_USER } from './gameTypes';

interface UserState {
  user: string;
}

const initialState: UserState = {
  user: 'player',
};

const gameReducer = (state = initialState, action: any) => {
  switch (action.type) {
    case SET_USER:
      return {
        ...state,
        user: action.payload,
      };
    default:
      return state;
  }
};

export default gameReducer;
