import { useEffect, useState } from 'react';
import { Alert, Button, Divider, Row, Space, notification } from 'antd';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { State, UserActions } from '../redux';
import { GithubOutlined, LoadingOutlined } from '@ant-design/icons';
import { GoogleCredentialResponse, GoogleLogin } from '@react-oauth/google';
import { jwtDecode } from 'jwt-decode';
import { Logo } from '.';
import axios from 'axios';

interface GoogleUser {
  name: string;
  email: string;
}

type LobbyState = 'LOBBY' | 'ACTIVE' | 'UNREACHABLE';

export const Login = () => {
  const user = useSelector((state: State) => state.user);
  const [signingIn, setSigningIn] = useState(false);
  const [lobbyState, setLobbyState] = useState<LobbyState | null>(null);
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

  // Checking if server is reachable using lobby state API
  useEffect(() => {
    axios
      .get('/api/game')
      .then(({ data }) => setLobbyState(data))
      .catch((error) => {
        console.error(error);
        setLobbyState('UNREACHABLE');
      });
  }, []);

  const handleGoogleLoginSuccess = ({
    credential,
  }: GoogleCredentialResponse) => {
    if (!credential) {
      handleGoogleLoginError();
      return;
    }
    const user: GoogleUser = jwtDecode(credential);
    setSigningIn(true);
    axios
      .post('/api/auth', user)
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
        setSigningIn(false);
      });
  };

  const handleGoogleLoginError = () => {
    const message =
      'Google authentication failed. Please contact the developers to address your issue!';
    api.error({ message });
    setSigningIn(false);
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
          <Row justify="center">
            {!signingIn && !!lobbyState && lobbyState !== 'UNREACHABLE' && (
              /**
               * Google Login documentation link -
               * https://www.npmjs.com/package/@react-oauth/google
               */
              <GoogleLogin
                onSuccess={handleGoogleLoginSuccess}
                onError={handleGoogleLoginError}
                theme="filled_black"
                size="medium"
                shape="rectangular"
                text={lobbyState === 'ACTIVE' ? 'continue_with' : 'signin_with'}
                useOneTap
              />
            )}
            {!!signingIn && (
              <Space>
                <LoadingOutlined />
                Signing you in...
              </Space>
            )}
            {!lobbyState && (
              <Space>
                <LoadingOutlined />
                Checking game's status...
              </Space>
            )}
          </Row>
          {(lobbyState === 'UNREACHABLE' || lobbyState === 'ACTIVE') && (
            <>
              {lobbyState === 'ACTIVE' && <br />}
              <Alert
                type={lobbyState === 'UNREACHABLE' ? 'error' : 'warning'}
                message={
                  lobbyState === 'UNREACHABLE'
                    ? 'Servers are unreachable!'
                    : 'Session is underway!'
                }
                description={
                  lobbyState === 'UNREACHABLE'
                    ? 'The game is presently in a developmental stage, and we frequently deactivate the servers to reduce expenses. If you wish to engage with The Strategists, kindly reach out to the developers for access.'
                    : "The game is presently undergoing development and can only accommodate one session simultaneously. If you're not involved in the ongoing session and want to enter The Strategists, kindly contact the developers."
                }
                showIcon
                banner
              />
            </>
          )}
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
