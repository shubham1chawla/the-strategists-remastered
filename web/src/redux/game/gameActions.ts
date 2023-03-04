import { SET_USER } from './gameTypes';

export const setUser = (user: string) => {
  return {
    type: SET_USER,
    payload: user,
  };
};
