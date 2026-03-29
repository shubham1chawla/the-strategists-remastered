import {
  createContext,
  PropsWithChildren,
  useEffect,
  useMemo,
  useState,
} from 'react';
import { useNavigate } from 'react-router-dom';
import useLoginState from '@login/hooks/useLoginState';

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
  recaptchaSiteKey: string | null;
  googleOAuthClientId: string | null;
  gameCodeLength: number;
  loginWorkflow: LoginWorkflow;
  setLoginWorkflow: (loginWorkflow: LoginWorkflow) => void;
  googleLoginCredential: string | null;
  setGoogleLoginCredential: (googleLoginCredential: string | null) => void;
}

export const LoginWorkflowContext =
  createContext<LoginWorkflowProviderValue | null>(null);

function LoginWorkflowProvider({ children }: PropsWithChildren) {
  // Checking reCAPTCHA site key from env variable
  const recaptchaSiteKey = useMemo(() => {
    const siteKey = (
      import.meta.env.VITE_GOOGLE_RECAPTCHA_SITE_KEY || ''
    ).trim();
    return !siteKey || siteKey.startsWith('PLACEHOLDER_') ? null : siteKey;
  }, []);

  // Checking Google OAuth client ID from env variable
  const googleOAuthClientId = useMemo(() => {
    const clientId = (
      import.meta.env?.VITE_GOOGLE_OAUTH_CLIENT_ID || ''
    ).trim();
    return !clientId || clientId.startsWith('PLACEHOLDER_') ? null : clientId;
  }, []);

  // Checking Game Code Length from env variable
  const gameCodeLength = useMemo(() => {
    const length = (import.meta.env?.VITE_GAME_CODE_LENGTH || '').trim();
    return !length || length.startsWith('PLACEHOLDER_')
      ? 4
      : parseInt(length, 10);
  }, []);

  // Assuming user to be not verified if reCAPTCHA is enabled
  const [loginWorkflow, setLoginWorkflow] = useState<LoginWorkflow>(
    !recaptchaSiteKey ? 'VERIFIED' : 'NOT_VERIFIED',
  );

  // Google Login credential either via Google or generated via Manual Login
  const [googleLoginCredential, setGoogleLoginCredential] = useState<
    string | null
  >(null);
  const { gameCode } = useLoginState();
  const navigate = useNavigate();

  // Creating context value
  const value: LoginWorkflowProviderValue = useMemo(
    () => ({
      recaptchaSiteKey,
      googleOAuthClientId,
      gameCodeLength,
      loginWorkflow,
      setLoginWorkflow,
      googleLoginCredential,
      setGoogleLoginCredential,
    }),
    [
      recaptchaSiteKey,
      googleOAuthClientId,
      googleLoginCredential,
      gameCodeLength,
      loginWorkflow,
    ],
  );

  // Redirecting to dashboard if user logged-in
  useEffect(() => {
    if (!gameCode) {
      return;
    }
    navigate('/game');
  }, [navigate, gameCode]);

  return (
    <LoginWorkflowContext.Provider value={value}>
      {children}
    </LoginWorkflowContext.Provider>
  );
}

export default LoginWorkflowProvider;
