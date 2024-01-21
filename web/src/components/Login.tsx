import { useEffect, useState } from 'react';
import { Button, Divider, Row, Space, notification } from 'antd';
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

export const Login = () => {
  const user = useSelector((state: State) => state.user);
  const [loading, setLoading] = useState(false);
  const [api, contextHolder] = notification.useNotification();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  // redirecting to dashboard if user logged-in
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
    setLoading(true);
    axios
      .post('/api/auth', user)
      .then(({ data }) => {
        dispatch(UserActions.setUser(data));
        navigate('/dashboard');
      })
      .catch(({ response }) => {
        const message =
          response.status === 404
            ? 'Incorrect credentials!'
            : 'Something went wrong, please try again later!';
        api.error({ message });
        setLoading(false);
      });
  };

  const handleGoogleLoginError = () => {
    const message =
      'Google authentication failed. Please contact the developers to address your issue!';
    api.error({ message });
    setLoading(false);
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
            {
              /**
               * Google Login documentation link -
               * https://www.npmjs.com/package/@react-oauth/google
               */
              !loading ? (
                <GoogleLogin
                  onSuccess={handleGoogleLoginSuccess}
                  onError={handleGoogleLoginError}
                  theme="filled_black"
                  size="medium"
                  shape="rectangular"
                  useOneTap
                />
              ) : (
                <Space>
                  <LoadingOutlined />
                  Signing you in...
                </Space>
              )
            }
          </Row>
          <br />
          <Divider>
            <Button
              target="_blank"
              href="https://github.com/shubham1chawla/the-strategists-remastered"
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
