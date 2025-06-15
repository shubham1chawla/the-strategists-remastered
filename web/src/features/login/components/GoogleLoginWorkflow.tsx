import { useCallback } from 'react';
import { GoogleCredentialResponse, GoogleLogin } from '@react-oauth/google';
import useLoginWorkflow from '@login/hooks/useLoginWorkflow';
import useNotifications from '@shared/hooks/useNotifications';

const GoogleLoginWorkflow = () => {
  const { loginWorkflow, setLoginWorkflow, setGoogleLoginCredential } =
    useLoginWorkflow();
  const { errorNotification } = useNotifications();

  const handleGoogleLoginError = useCallback(() => {
    errorNotification({
      message:
        'Google authentication failed. Please contact the developers to address your issue!',
    });
    setLoginWorkflow('VERIFIED');
  }, [setLoginWorkflow, errorNotification]);

  const handleGoogleLoginSuccess = useCallback(
    ({ credential }: GoogleCredentialResponse) => {
      if (!credential) {
        handleGoogleLoginError();
        return;
      }
      setGoogleLoginCredential(credential);
      setLoginWorkflow('RESUME');
    },
    [handleGoogleLoginError, setLoginWorkflow, setGoogleLoginCredential],
  );

  if (loginWorkflow !== 'VERIFIED') return null;
  return (
    <GoogleLogin
      onSuccess={handleGoogleLoginSuccess}
      onError={handleGoogleLoginError}
      theme="filled_black"
      size="medium"
      shape="rectangular"
      text="signin_with"
      useOneTap
    />
  );
};

export default GoogleLoginWorkflow;
