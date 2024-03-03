import { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import {
  Alert,
  Button,
  Card,
  Divider,
  Form,
  Input,
  Row,
  Space,
  notification,
} from 'antd';
import {
  AppstoreAddOutlined,
  AppstoreOutlined,
  ArrowLeftOutlined,
  GithubOutlined,
  LoadingOutlined,
} from '@ant-design/icons';
import { GoogleCredentialResponse, GoogleLogin } from '@react-oauth/google';
import { Logo } from '.';
import { LoginActions, LoginState, useLogin } from '../redux';
import ReCAPTCHA from 'react-google-recaptcha';
import axios from 'axios';

type Workflow =
  | 'NOT_VERIFIED'
  | 'VERIFYING'
  | 'UNREACHABLE'
  | 'VERIFIED'
  | 'RESUME'
  | 'ACTIONS'
  | 'JOIN_ACTION'
  | 'ENTERING';

export const Login = () => {
  const { gameCode } = useLogin();
  const [workflow, setWorkflow] = useState<Workflow>('NOT_VERIFIED');
  const [credential, setCredential] = useState<string | null>(null);
  const [joinDisabled, setJoinDisabled] = useState(true);
  const [api, contextHolder] = notification.useNotification();
  const [form] = Form.useForm<{ code: string }>();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  // Redirecting to dashboard if user logged-in
  useEffect(() => {
    if (!gameCode) {
      return;
    }
    navigate('/dashboard');
  }, [navigate, gameCode]);

  // Checking if join form should be enabled
  const joinFormValues = Form.useWatch([], form);
  useEffect(() => {
    const code: string | undefined = form.getFieldValue('code');
    if (code) {
      form.setFieldValue('code', code.toUpperCase());
      form
        .validateFields({ validateOnly: true })
        .then(() => setJoinDisabled(false))
        .catch(() => setJoinDisabled(true));
    } else {
      setJoinDisabled(true);
    }
  }, [form, joinFormValues]);

  // Checking if player already part of a game
  useEffect(() => {
    if (workflow !== 'RESUME') return;
    axios
      .get<LoginState>(`/api/games?credential=${credential}`)
      .then(({ data }) => {
        dispatch(LoginActions.login(data));
        navigate('/dashboard');
      })
      .catch(() => setWorkflow('ACTIONS'));
  }, [workflow, credential, dispatch, navigate]);

  const handleGoogleLoginSuccess = ({
    credential,
  }: GoogleCredentialResponse) => {
    if (!credential) {
      handleGoogleLoginError();
      return;
    }
    setCredential(credential);
    setWorkflow('RESUME');
  };

  const handleGoogleLoginError = () => {
    const message =
      'Google authentication failed. Please contact the developers to address your issue!';
    api.error({ message });
    setWorkflow('VERIFIED');
  };

  const handleEnterAction = (code?: string) => {
    setWorkflow('ENTERING');
    const url = code ? `/api/games/${code}/players` : '/api/games';
    axios
      .post<LoginState>(url, { credential })
      .then(({ data }) => {
        dispatch(LoginActions.login(data));
        navigate('/dashboard');
      })
      .catch(({ response }) => {
        switch (response.status) {
          case 404:
            api.error({ message: 'No game found for the entered code!' });
            break;
          case 403:
            api.error({
              message: 'You are not authorized to create a game!',
              description: 'Please contact the developers to grant access.',
            });
            break;
          default:
            api.error({
              message: 'Unable to join the game!',
              description:
                'Please contact the developers if this problem persists.',
            });
        }
        setWorkflow(code ? 'JOIN_ACTION' : 'ACTIONS');
      });
  };

  const getWorkflowComponent = () => {
    switch (workflow) {
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
      case 'RESUME':
        return (
          <Space>
            <LoadingOutlined />
            Checking if you are part of any game...
          </Space>
        );
      case 'ACTIONS':
        return (
          <Space>
            <Card
              className="strategists-login__workflows__card"
              onClick={() => handleEnterAction()}
              hoverable
            >
              <Card.Meta
                title={
                  <Space>
                    <AppstoreAddOutlined />
                    Create Game
                  </Space>
                }
                description="A new session for you and your friends to play The Strategists!"
              />
            </Card>
            <Card
              className="strategists-login__workflows__card"
              onClick={() => setWorkflow('JOIN_ACTION')}
              hoverable
            >
              <Card.Meta
                title={
                  <Space>
                    <AppstoreOutlined />
                    Join Game
                  </Space>
                }
                description="Enter existing session hosted by your friends to play The Strategists!"
              />
            </Card>
          </Space>
        );
      case 'JOIN_ACTION':
        return (
          <div className="strategists-login__workflows__join">
            <Card bordered={false} size="small">
              <Card.Meta
                title={
                  <Space>
                    <Button
                      className="strategists-login__workflows__join__back-button"
                      size="large"
                      type="link"
                      icon={<ArrowLeftOutlined />}
                      onClick={() => setWorkflow('ACTIONS')}
                    >
                      Join Game
                    </Button>
                  </Space>
                }
              />
              <Form
                className="strategists-login__workflows__join__form"
                form={form}
                layout="inline"
                onFinish={({ code }) => handleEnterAction(code)}
                onFinishFailed={console.error}
              >
                <Space.Compact>
                  <Form.Item
                    name="code"
                    rules={[
                      {
                        required: true,
                        type: 'string',
                        len: 4,
                        whitespace: false,
                        pattern: /[A-Z]/,
                        validateTrigger: ['onChange', 'onBlur'],
                      },
                    ]}
                    noStyle
                  >
                    <Input
                      type="text"
                      placeholder="Enter game's code"
                      prefix={<AppstoreOutlined />}
                      size="large"
                      autoFocus
                      required
                    />
                  </Form.Item>
                  <Form.Item noStyle>
                    <Button
                      htmlType="submit"
                      type="primary"
                      size="large"
                      disabled={joinDisabled}
                    >
                      Join
                    </Button>
                  </Form.Item>
                </Space.Compact>
              </Form>
            </Card>
          </div>
        );
      case 'ENTERING':
        return (
          <Space>
            <LoadingOutlined />
            Entering...
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
        <section className="strategists-login__workflows strategists-glossy">
          <Divider>
            <Logo />
          </Divider>
          <br />
          <Row justify="center">
            {/**
             * Had to move the ReCAPTCHA component out of the 'getWorkflowComponent'
             * to avoid the following issue. Read more about it here.
             * https://github.com/google/recaptcha/issues/269#issuecomment-606838861
             */}
            <ReCAPTCHA
              style={{
                display: workflow === 'NOT_VERIFIED' ? 'block' : 'none',
              }}
              sitekey={process.env.REACT_APP_GOOGLE_RECAPTCHA_SITE_KEY || ''}
              theme="dark"
              onChange={(clientToken) => {
                if (!clientToken) return;
                setWorkflow('VERIFYING');
                axios
                  .post('/api/recaptcha', { clientToken })
                  .then(() => setWorkflow('VERIFIED'))
                  .catch(({ response }) => {
                    const { status } = response;
                    setWorkflow(
                      status === 403 ? 'NOT_VERIFIED' : 'UNREACHABLE'
                    );
                  });
              }}
              onExpired={() => setWorkflow('NOT_VERIFIED')}
            />
            {getWorkflowComponent()}
          </Row>
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
