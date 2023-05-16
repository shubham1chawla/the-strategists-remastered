import { Dispatch, useEffect } from 'react';
import { Button, Col, Row, Tabs, TabsProps, Tooltip, notification } from 'antd';
import { Actions, Activity, Logo, Lobby, Map } from '.';
import { useDispatch, useSelector } from 'react-redux';
import { ActivityActions, LobbyActions, State } from '../redux';
import { FireOutlined, LogoutOutlined, RocketFilled } from '@ant-design/icons';
import axios from 'axios';

const getCalls = {
  '/api/players': LobbyActions.setPlayers, // Updating players
  '/api/activities': ActivityActions.setActivities, // Updating players
  '/api/lands': LobbyActions.setLands, // Updating lands
  '/api/game': LobbyActions.setState, // Updating state
};

export const Dashboard = () => {
  const { username, type } = useSelector((state: State) => state.user);
  const [api, contextHolder] = notification.useNotification();
  const dispatch = useDispatch();

  useEffect(() => {
    // Syncing game's state
    for (const [api, action] of Object.entries(getCalls)) {
      axios.get(api).then(({ data }) => dispatch(action(data)));
    }

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
        {Navigation(type, dispatch)}
        {type === 'admin' ? AdminPanel() : PlayerPanel()}
      </Col>
      <Col className="strategists-dashboard__right-section" flex="70%">
        <Map />
      </Col>
    </Row>
  );
};

const Navigation = (type: 'admin' | 'player', dispatch: Dispatch<any>) => {
  const { state, players } = useSelector((state: State) => state.lobby);

  const start = async () => {
    if (state === 'active') return;
    await axios.put('/api/game/start');
    dispatch(LobbyActions.setState('active'));
  };

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
        <Tooltip
          title={
            !players.length
              ? 'Add players to start The Strategists!'
              : state === 'active'
              ? 'The Strategists in progress!'
              : 'Start The Strategists!'
          }
        >
          <Button
            disabled={state === 'active' || !players.length}
            type="primary"
            htmlType="submit"
            onClick={() => start()}
          >
            {state === 'active' ? <FireOutlined /> : <RocketFilled />}
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
