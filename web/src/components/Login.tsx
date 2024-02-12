import { useEffect, useState } from 'react';
import { Alert, Button, Divider, Row, Space, notification } from 'antd';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { State, UserActions } from '../redux';
import { GithubOutlined, LoadingOutlined } from '@ant-design/icons';
import { GoogleCredentialResponse, GoogleLogin } from '@react-oauth/google';
import { jwtDecode } from 'jwt-decode';
import { Logo } from '.';
import ReCAPTCHA from 'react-google-recaptcha';
import axios from 'axios';

interface GoogleUser {
  name: string;
  email: string;
}

type Workflow =
  | 'NOT_VERIFIED'
  | 'VERIFYING'
  | 'UNREACHABLE'
  | 'VERIFIED'
  | 'SIGNING_IN';

export const Login = () => {
  const user = useSelector((state: State) => state.user);
  const [workflow, setWorkflow] = useState<Workflow>('NOT_VERIFIED');
  const [api, contextHolder] = notification.useNotification();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  // Redirecting to dashboard if user logged-in
  useEffect(() => {
    if (!user.username) {
      return;
    }
    navigate('/dashboard');
  }, [navigate, user.username]);

  const handleGoogleLoginSuccess = ({
    credential,
  }: GoogleCredentialResponse) => {
    if (!credential) {
      handleGoogleLoginError();
      return;
    }
    const user: GoogleUser = jwtDecode(credential);
    setWorkflow('SIGNING_IN');
    axios
      .post('/api/authenticate', user)
      .then(({ data }) => {
        dispatch(UserActions.setUser(data));
        navigate('/dashboard');
      })
      .catch(({ response }) => {
        const message =
          response.status === 404
            ? 'Your email is not included in the ongoing session of The Strategists!'
            : 'Something went wrong, please try again later!';
        api.error({ message });
        setWorkflow('VERIFIED');
      });
  };

  const handleGoogleLoginError = () => {
    const message =
      'Google authentication failed. Please contact the developers to address your issue!';
    api.error({ message });
    setWorkflow('VERIFIED');
  };

  const getWorkflowComponent = () => {
    switch (workflow) {
      case 'NOT_VERIFIED':
        return (
          <ReCAPTCHA
            sitekey={process.env.REACT_APP_GOOGLE_RECAPTCHA_SITE_KEY || ''}
            theme="dark"
            onChange={(clientToken) => {
              setWorkflow('VERIFYING');
              axios
                .post('/api/recaptcha', { clientToken })
                .then(() => setWorkflow('VERIFIED'))
                .catch(({ response }) => {
                  const { status } = response;
                  setWorkflow(status === 403 ? 'NOT_VERIFIED' : 'UNREACHABLE');
                });
            }}
          />
        );
      case 'VERIFYING':
        return (
          <Space>
            <LoadingOutlined />
            Verifying...
          </Space>
        );
      case 'VERIFIED':
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
      case 'SIGNING_IN':
        return (
          <Space>
            <LoadingOutlined />
            Signing you in...
          </Space>
        );
      case 'UNREACHABLE':
        return (
          <Alert
            type="error"
            message="Servers are unreachable!"
            description="The game is presently in a developmental stage, and we frequently deactivate the servers to reduce expenses. If you wish to engage with The Strategists, kindly reach out to the developers for access."
            showIcon
            banner
          />
        );
    }
  };

  return (
    <>
      {contextHolder}
      <main className="strategists-login strategists-wallpaper">
        <section className="strategists-login__card strategists-glossy">
          <Divider>
            <Logo />
          </Divider>
          <br />
          <Row justify="center">{getWorkflowComponent()}</Row>
          <br />
          <Divider>
            <Button
              target="_blank"
              href="https://github.com/shubham1chawla/the-strategists-remastered/issues"
              type="text"
              icon={<GithubOutlined />}
            >
              Contact
            </Button>
          </Divider>
        </section>
      </main>
    </>
  );
};
