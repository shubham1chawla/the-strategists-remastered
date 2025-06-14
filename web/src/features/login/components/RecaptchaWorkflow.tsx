import { useCallback } from 'react';
import ReCAPTCHA from 'react-google-recaptcha';
import useLoginWorkflow from '@login/hooks/useLoginWorkflow';
import axios from 'axios';

const RecaptchaWorkflow = () => {
  const { loginWorkflow, setLoginWorkflow } = useLoginWorkflow();

  const onRecaptchaChange = useCallback(
    (clientToken: string | null) => {
      if (!clientToken) return;
      setLoginWorkflow('VERIFYING');
      axios
        .post('/api/recaptcha', { clientToken })
        .then(() => setLoginWorkflow('VERIFIED'))
        .catch(({ response: { status } }) =>
          setLoginWorkflow(status === 403 ? 'NOT_VERIFIED' : 'UNREACHABLE'),
        );
    },
    [setLoginWorkflow],
  );

  /**
   * ReCAPTCHA should not be conditionally rendered!
   * https://github.com/google/recaptcha/issues/269#issuecomment-606838861
   */
  return (
    <ReCAPTCHA
      style={{
        display: loginWorkflow === 'NOT_VERIFIED' ? 'block' : 'none',
      }}
      sitekey={process.env.REACT_APP_GOOGLE_RECAPTCHA_SITE_KEY || ''}
      theme="dark"
      onChange={onRecaptchaChange}
      onExpired={() => setLoginWorkflow('NOT_VERIFIED')}
    />
  );
};

export default RecaptchaWorkflow;
