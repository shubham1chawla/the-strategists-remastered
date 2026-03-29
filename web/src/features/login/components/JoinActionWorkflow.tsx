import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { Button, Col, Flex, Form, Input, Row } from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import axios from 'axios';
import useNotifications from '@shared/hooks/useNotifications';
import useLoginWorkflow from '@login/hooks/useLoginWorkflow';
import { loggedIn, LoginState } from '@login/state';

function JoinActionWorkflow() {
  const {
    loginWorkflow,
    googleLoginCredential,
    gameCodeLength,
    setLoginWorkflow,
  } = useLoginWorkflow();
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
        .post<LoginState>(`/api/games/${code.toUpperCase()}/players`, {
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
                title: 'No game found for the entered code!',
              });
              break;
            default:
              errorNotification({
                title: 'Unable to join the game!',
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
    <Flex orientation="vertical">
      <Row justify="start">
        <Col>
          <Button
            size="large"
            type="text"
            icon={<ArrowLeftOutlined />}
            onClick={() => setLoginWorkflow('ACTIONS')}
          >
            Back
          </Button>
        </Col>
      </Row>
      <Row justify="center">
        <Col span={15}>
          <Form
            layout="vertical"
            autoComplete="off"
            size="large"
            requiredMark={false}
            form={form}
            onFinish={({ code }) => handleJoinAction(code)}
          >
            <Form.Item
              name="code"
              label="Game's Code"
              rules={[
                {
                  required: true,
                  type: 'string',
                  len: gameCodeLength,
                  whitespace: false,
                  pattern: /[A-Za-z]/,
                  validateTrigger: ['onChange', 'onBlur'],
                },
              ]}
            >
              <Input.OTP
                size="large"
                autoFocus
                formatter={(code) => code.toUpperCase()}
                separator={<span>/</span>}
                length={gameCodeLength}
              />
            </Form.Item>
            <Form.Item>
              <Button
                htmlType="submit"
                type="primary"
                size="large"
                disabled={joinButtonDisabled}
                block
              >
                Join
              </Button>
            </Form.Item>
          </Form>
        </Col>
      </Row>
    </Flex>
  );
}

export default JoinActionWorkflow;
