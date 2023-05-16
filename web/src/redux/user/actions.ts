import { UserState } from '.';

export namespace UserActions {
  export const Types = {
    SET_USER: 'SET_USER',
    UNSET_USER: 'UNSET_USER',
  };

  export const setUser = (user: UserState) => {
    return {
      type: Types.SET_USER,
      payload: user,
    };
  };

  export const unsetUser = () => {
    return {
      type: Types.UNSET_USER,
    };
  };
}
