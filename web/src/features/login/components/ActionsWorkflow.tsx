import { useCallback } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { Card, Space } from 'antd';
import { AppstoreAddOutlined, AppstoreOutlined } from '@ant-design/icons';
import useLoginWorkflow from '@login/hooks/useLoginWorkflow';
import { loggedIn, LoginState } from '@login/state';
import useNotifications from '@shared/hooks/useNotifications';
import axios from 'axios';

const ActionsWorklfow = () => {
  const { loginWorkflow, googleLoginCredential, setLoginWorkflow } =
    useLoginWorkflow();
  const { errorNotification } = useNotifications();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleCreateAction = useCallback(() => {
    setLoginWorkflow('ENTERING');
    axios
      .post<LoginState>('/api/games', { credential: googleLoginCredential })
      .then(({ data }) => {
        dispatch(loggedIn(data));
        navigate('/game');
      })
      .catch(({ response }) => {
        switch (response.status) {
          case 403:
            errorNotification({
              message: 'You are not authorized to create a game!',
              description: 'Please contact the developers to grant access.',
            });
            break;
          default:
            errorNotification({
              message: 'Unable to create the game!',
              description:
                'Please contact the developers if this problem persists.',
            });
        }
        setLoginWorkflow('ACTIONS');
      });
  }, [
    googleLoginCredential,
    setLoginWorkflow,
    errorNotification,
    dispatch,
    navigate,
  ]);

  if (loginWorkflow !== 'ACTIONS') return null;
  return (
    <Space>
      <Card
        className="strategists-login__workflows__card"
        onClick={() => handleCreateAction()}
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
        onClick={() => setLoginWorkflow('JOIN_ACTION')}
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
};

export default ActionsWorklfow;
