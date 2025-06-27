import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { Button, Card, Form, Input, Space } from 'antd';
import { AppstoreOutlined, ArrowLeftOutlined } from '@ant-design/icons';
import axios from 'axios';
import useNotifications from '@shared/hooks/useNotifications';
import useLoginWorkflow from '@login/hooks/useLoginWorkflow';
import { loggedIn, LoginState } from '@login/state';

function JoinActionWorkflow() {
  const { loginWorkflow, googleLoginCredential, setLoginWorkflow } =
    useLoginWorkflow();
  const { errorNotification } = useNotifications();
  const [form] = Form.useForm<{ code: string }>();
  const [joinButtonDisabled, setJoinButtonDisabled] = useState(true);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  // Checking if join form should be enabled
  const joinFormValues = Form.useWatch([], form);
  useEffect(() => {
    const code: string | undefined = form.getFieldValue('code');
    if (code) {
      form.setFieldValue('code', code.toUpperCase());
      form
        .validateFields({ validateOnly: true })
        .then(() => setJoinButtonDisabled(false))
        .catch(() => setJoinButtonDisabled(true));
    } else {
      setJoinButtonDisabled(true);
    }
  }, [form, joinFormValues]);

  const handleJoinAction = useCallback(
    (code: string) => {
      setLoginWorkflow('ENTERING');
      axios
        .post<LoginState>(`/api/games/${code}/players`, {
          credential: googleLoginCredential,
        })
        .then(({ data }) => {
          dispatch(loggedIn(data));
          navigate('/game');
        })
        .catch(({ response }) => {
          switch (response.status) {
            case 404:
              errorNotification({
                message: 'No game found for the entered code!',
              });
              break;
            default:
              errorNotification({
                message: 'Unable to join the game!',
                description:
                  'Please contact the developers if this problem persists.',
              });
          }
          setLoginWorkflow(code ? 'JOIN_ACTION' : 'ACTIONS');
        });
    },
    [
      googleLoginCredential,
      setLoginWorkflow,
      errorNotification,
      dispatch,
      navigate,
    ],
  );

  if (loginWorkflow !== 'JOIN_ACTION') return null;
  return (
    <div className="strategists-login__workflows__join">
      <Card variant="borderless" size="small">
        <Card.Meta
          title={
            <Space>
              <Button
                className="strategists-login__workflows__join__back-button"
                size="large"
                type="link"
                icon={<ArrowLeftOutlined />}
                onClick={() => setLoginWorkflow('ACTIONS')}
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
          onFinish={({ code }) => handleJoinAction(code)}
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
                disabled={joinButtonDisabled}
              >
                Join
              </Button>
            </Form.Item>
          </Space.Compact>
        </Form>
      </Card>
    </div>
  );
}

export default JoinActionWorkflow;
