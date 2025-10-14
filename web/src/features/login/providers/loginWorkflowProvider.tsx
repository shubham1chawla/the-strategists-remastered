import {
  createContext,
  PropsWithChildren,
  useEffect,
  useMemo,
  useState,
} from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import axios from 'axios';
import useLoginState from '@login/hooks/useLoginState';
import { loggedIn, LoginState } from '@login/state';

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

function LoginWorkflowProvider({ children }: PropsWithChildren) {
  const [loginWorkflow, setLoginWorkflow] =
    useState<LoginWorkflow>('NOT_VERIFIED');
  const [googleLoginCredential, setGoogleLoginCredential] = useState<
    string | null
  >(null);
  const { gameCode } = useLoginState();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const value: LoginWorkflowProviderValue = useMemo(
    () => ({
      loginWorkflow,
      setLoginWorkflow,
      googleLoginCredential,
      setGoogleLoginCredential,
    }),
    [googleLoginCredential, loginWorkflow],
  );

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
}

export default LoginWorkflowProvider;
