import { UserState, SET_USER } from '.';

export const setUser = (user: UserState) => {
  return {
    type: SET_USER,
    payload: user,
  };
};
