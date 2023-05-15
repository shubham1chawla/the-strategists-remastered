import { useEffect } from 'react';
import { Button, Col, Row, Tabs, TabsProps, Tooltip, notification } from 'antd';
import { Actions, Activity, Logo, Lobby, Map } from '.';
import { useDispatch, useSelector } from 'react-redux';
import { ActivityActions, LobbyActions, State } from '../redux';
import { LogoutOutlined, RocketFilled } from '@ant-design/icons';
import axios from 'axios';

export const Dashboard = () => {
  const { username, type } = useSelector((state: State) => state.user);
  const [api, contextHolder] = notification.useNotification();
  const dispatch = useDispatch();

  useEffect(() => {
    // Updating players
    axios
      .get('/api/players')
      .then(({ data }) => dispatch(LobbyActions.setPlayers(data)));

    // Updating activities
    axios
      .get('/api/activities')
      .then(({ data }) => dispatch(ActivityActions.setActivities(data)));

    // Updating lands
    axios
      .get('/api/lands')
      .then(({ data }) => dispatch(LobbyActions.setLands(data)));

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
          api.open({ message: data });
          break;
        default:
          console.warn(`Unsupported update type: ${type}`);
      }
    };
    updates.onerror = console.error;
  }, [dispatch, username, api]);

  return (
    <Row className="strategists-dashboard">
      {contextHolder}
      <Col className="strategists-dashboard__left-section" flex="30%">
        {Navigation(type)}
        {type === 'admin' ? AdminPanel() : PlayerPanel()}
      </Col>
      <Col className="strategists-dashboard__right-section" flex="70%">
        <Map />
      </Col>
    </Row>
  );
};

const Navigation = (type: 'admin' | 'player') => {
  return (
    <nav className="strategists-nav">
      <header className="strategists-header">
        <Tooltip title="Logout">
          <Button
            size="large"
            type="text"
            shape="circle"
            icon={<LogoutOutlined />}
          />
        </Tooltip>
        <Logo />
      </header>
      {type === 'admin' ? (
        <Tooltip title="Start the game!">
          <Button type="primary" htmlType="submit">
            <RocketFilled />
          </Button>
        </Tooltip>
      ) : null}
    </nav>
  );
};

const AdminPanel = () => {
  const items: TabsProps['items'] = [
    {
      key: '1',
      label: `Lobby`,
      children: <Lobby />,
      className: 'strategists-tab-body strategists-lobby',
    },
    {
      key: '2',
      label: `Activities`,
      children: <Activity />,
      className: 'strategists-tab-body strategists-activity',
    },
    {
      key: '3',
      label: `Events`,
      children: `Content of Tab Events`,
    },
  ];
  return <Tabs centered defaultActiveKey="1" size="large" items={items} />;
};

const PlayerPanel = () => {
  return (
    <div className="strategists-player-panel">
      <Activity />
      <Actions />
    </div>
  );
};
