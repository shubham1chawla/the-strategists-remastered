import { LoginState } from '.';

export namespace LoginActions {
  export const Types = {
    LOGIN: 'LOGIN',
    LOGOUT: 'LOGOUT',
  };

  export const login = (state: LoginState) => {
    return {
      type: Types.LOGIN,
      payload: state,
    };
  };

  export const logout = () => {
    return {
      type: Types.LOGOUT,
    };
  };
}
