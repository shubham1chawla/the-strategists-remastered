import { useEffect } from 'react';
import { Col, Row, Tabs, TabsProps, notification } from 'antd';
import { Actions, Activity, Lobby, Map } from '.';
import { useDispatch, useSelector } from 'react-redux';
import { ActivityActions, LobbyActions, Player } from '../redux';
import axios from 'axios';

export const Dashboard = () => {
  const { username, type } = useSelector((state: any) => state.user);
  const dispatch = useDispatch();

  useEffect(() => {
    // Updating players list
    axios.get('/api/players').then(async ({ data }) => {
      await data.forEach((player: Player) => {
        dispatch(LobbyActions.addPlayer(player));
      });
    });

    // Setting up SSE for updates
    const updates = new EventSource(
      `${process.env.REACT_APP_API_BASE_URL}/api/updates/${username}`
    );
    updates.onmessage = (message: MessageEvent<any>) => {
      const { type, data } = JSON.parse(message.data);
      switch (type) {
        case 'JOIN':
          dispatch(LobbyActions.addPlayer(data));
          break;
        case 'KICK':
          dispatch(LobbyActions.kickPlayer(data));
          break;
        case 'NEW':
          dispatch(ActivityActions.addActivity(data));
          notification.open({ message: data });
          break;
        default:
          console.warn(`Unsupported update type: ${type}`);
      }
    };
    updates.onerror = console.error;
  }, [dispatch, username]);

  return (
    <Row className="strategists-dashboard">
      <Col className="strategists-dashboard__left-section" flex="30%">
        {type === 'admin' ? renderAdminPanel() : renderPlayerPanel()}
      </Col>
      <Col className="strategists-dashboard__right-section" flex="70%">
        <Map />
      </Col>
    </Row>
  );
};

const renderAdminPanel = () => {
  const items: TabsProps['items'] = [
    {
      key: '1',
      label: `Lobby`,
      children: <Lobby />,
      className: 'strategists-lobby',
    },
    {
      key: '2',
      label: `Activities`,
      children: <Activity />,
      className: 'strategists-activity',
    },
    {
      key: '3',
      label: `Events`,
      children: `Content of Tab Events`,
    },
  ];
  return <Tabs centered defaultActiveKey="1" size="large" items={items} />;
};

const renderPlayerPanel = () => {
  return (
    <div className="strategists-player-panel">
      <Activity />
      <Actions />
    </div>
  );
};
