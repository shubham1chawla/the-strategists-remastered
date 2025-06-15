import { createContext, PropsWithChildren, useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import useLogin from '@login/hooks/useLogin';
import { loggedIn, LoginState } from '@login/state';
import axios from 'axios';

type LoginWorkflow =
  | 'NOT_VERIFIED'
  | 'VERIFYING'
  | 'UNREACHABLE'
  | 'VERIFIED'
  | 'RESUME'
  | 'ACTIONS'
  | 'JOIN_ACTION'
  | 'ENTERING';

interface LoginWorkflowProviderValue {
  loginWorkflow: LoginWorkflow;
  setLoginWorkflow: (loginWorkflow: LoginWorkflow) => void;
  googleLoginCredential: string | null;
  setGoogleLoginCredential: (googleLoginCredential: string | null) => void;
}

export const LoginWorkflowContext =
  createContext<LoginWorkflowProviderValue | null>(null);

const LoginWorkflowProvider = ({ children }: PropsWithChildren) => {
  const [loginWorkflow, setLoginWorkflow] =
    useState<LoginWorkflow>('NOT_VERIFIED');
  const [googleLoginCredential, setGoogleLoginCredential] = useState<
    string | null
  >(null);
  const { gameCode } = useLogin();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const value: LoginWorkflowProviderValue = {
    loginWorkflow,
    setLoginWorkflow,
    googleLoginCredential,
    setGoogleLoginCredential,
  };

  // Redirecting to dashboard if user logged-in
  useEffect(() => {
    if (!gameCode) {
      return;
    }
    navigate('/game');
  }, [navigate, gameCode]);

  // Checking if player already part of a game
  useEffect(() => {
    if (loginWorkflow !== 'RESUME') return;
    axios
      .get<LoginState>(`/api/games?credential=${googleLoginCredential}`)
      .then(({ data }) => {
        dispatch(loggedIn(data));
        navigate('/game');
      })
      .catch(() => setLoginWorkflow('ACTIONS'));
  }, [
    loginWorkflow,
    setLoginWorkflow,
    googleLoginCredential,
    dispatch,
    navigate,
  ]);

  return (
    <LoginWorkflowContext.Provider value={value}>
      {children}
    </LoginWorkflowContext.Provider>
  );
};

export default LoginWorkflowProvider;
