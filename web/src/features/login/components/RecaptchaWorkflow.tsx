import { useCallback, useRef } from 'react';
import axios from 'axios';
import ReCAPTCHA from 'react-google-recaptcha';
import useNotifications from '@shared/hooks/useNotifications';
import useLoginWorkflow from '@login/hooks/useLoginWorkflow';

function RecaptchaWorkflow() {
  const { loginWorkflow, setLoginWorkflow, recaptchaSiteKey } =
    useLoginWorkflow();
  const { errorNotification } = useNotifications();
  const recaptchaRef = useRef<ReCAPTCHA | null>(null);

  const onRecaptchaChange = useCallback(
    (clientToken: string | null) => {
      if (!clientToken) return;
      setLoginWorkflow('VERIFYING');
      axios
        .post('/api/google-recaptcha-verify', { clientToken })
        .then(() => setLoginWorkflow('VERIFIED'))
        .catch(({ response: { status } }) => {
          setLoginWorkflow(status === 403 ? 'NOT_VERIFIED' : 'UNREACHABLE');
          errorNotification({
            message:
              status === 403
                ? 'Unable to verify reCAPTCHA. Please try again.'
                : 'Backend services are unreachable.',
          });
          recaptchaRef.current?.reset();
        });
    },
    [setLoginWorkflow, errorNotification],
  );

  // Skip rendering reCAPTCHA if site key is not configured
  if (!recaptchaSiteKey) return null;

  // Only render reCAPTCHA when user is not verified
  return (
    <ReCAPTCHA
      ref={recaptchaRef}
      style={{
        display: loginWorkflow === 'NOT_VERIFIED' ? 'block' : 'none',
      }}
      sitekey={recaptchaSiteKey || ''}
      theme="dark"
      onChange={onRecaptchaChange}
      onExpired={() => setLoginWorkflow('NOT_VERIFIED')}
    />
  );
}

export default RecaptchaWorkflow;
