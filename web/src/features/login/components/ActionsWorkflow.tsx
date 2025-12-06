import { useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { Flex, Space } from 'antd';
import { AppstoreAddOutlined, AppstoreOutlined } from '@ant-design/icons';
import axios from 'axios';
import CardButton from '@shared/components/CardButton';
import useNotifications from '@shared/hooks/useNotifications';
import useLoginWorkflow from '@login/hooks/useLoginWorkflow';
import { loggedIn, LoginState } from '@login/state';

function ActionsWorklfow() {
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
              title: 'You are not authorized to create a game!',
              description:
                'Please contact the developers to grant access or join existing games.',
            });
            break;
          default:
            errorNotification({
              title: 'Unable to create a game!',
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

  const handleJoinAction = useCallback(
    () => setLoginWorkflow('JOIN_ACTION'),
    [setLoginWorkflow],
  );

  if (loginWorkflow !== 'ACTIONS') return null;
  return (
    <Flex gap="large">
      <CardButton
        title={
          <Space>
            <AppstoreAddOutlined />
            Create Game
          </Space>
        }
        description="Create a new game session and invite your friends to join you."
        onClickOrEnter={handleCreateAction}
      />
      <CardButton
        title={
          <Space>
            <AppstoreOutlined />
            Join Game
          </Space>
        }
        description="Join an existing game session and play with them your friends."
        onClickOrEnter={handleJoinAction}
      />
    </Flex>
  );
}

export default ActionsWorklfow;
