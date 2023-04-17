import { UserState } from '.';

export namespace UserActions {
  export const Types = {
    SET_USER: 'SET_USER',
  };

  export const setUser = (user: UserState) => {
    return {
      type: Types.SET_USER,
      payload: user,
    };
  };
}
