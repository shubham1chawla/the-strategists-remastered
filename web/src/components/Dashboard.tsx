import { Dispatch, useEffect, useState } from 'react';
import { AnyAction } from 'redux';
import { useNavigate } from 'react-router-dom';
import { Button, Col, Row, Tabs, Tooltip, notification } from 'antd';
import {
  Actions,
  ActivityTimeline,
  Logo,
  Lobby,
  Map,
  Stats,
  ResetModal,
} from '.';
import { useDispatch, useSelector } from 'react-redux';
import {
  ActivityActions,
  LobbyActions,
  Player,
  State,
  UserActions,
  parseActivity,
} from '../redux';
import {
  LogoutOutlined,
  PlayCircleFilled,
  StopFilled,
} from '@ant-design/icons';
import axios from 'axios';

const getCalls = {
  '/api/players': LobbyActions.setPlayers, // Updating players
  '/api/activities': ActivityActions.setActivities, // Updating players
  '/api/lands': LobbyActions.setLands, // Updating lands
  '/api/game': LobbyActions.setState, // Updating state
};

const syncGameStates = (dispatch: Dispatch<AnyAction>) => {
  for (const [api, action] of Object.entries(getCalls)) {
    axios.get(api).then(({ data }) => dispatch(action(data)));
  }
};

export const Dashboard = () => {
  const { username, type } = useSelector((state: State) => state.user);
  const [api, contextHolder] = notification.useNotification();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const alertUser = (event: any) => {
    event.preventDefault();
    return (event.returnValue = '');
  };

  useEffect(() => {
    // Checking if player is logged in
    if (!username) {
      navigate('/login');
      return;
    }

    // Syncing game's state
    syncGameStates(dispatch);

    // Setting up SSE for updates
    const updates = new EventSource(
      `${process.env.REACT_APP_API_BASE_URL}/api/updates/${username}`
    );
    updates.onmessage = (message: MessageEvent<any>) => {
      const { type, data, activity } = JSON.parse(message.data);
      switch (type) {
        case 'BANKRUPTCY': {
          const { lands, players } = data;
          dispatch(LobbyActions.patchLands(lands));
          dispatch(LobbyActions.patchPlayers(players));

          // Skipping turn if current player declared bankruptcy
          for (const p of players as Player[]) {
            if (p.turn && p.username === username && p.state === 'BANKRUPT') {
              axios.put('/api/game/next');
              break;
            }
          }
          break;
        }
        case 'INVEST': {
          const { land, players } = data;
          dispatch(LobbyActions.patchLands([land]));
          dispatch(LobbyActions.patchPlayers(players));
          break;
        }
        case 'JOIN':
          dispatch(LobbyActions.addPlayer(data));
          break;
        case 'KICK':
          dispatch(LobbyActions.kickPlayer(data));
          break;
        case 'MOVE':
          dispatch(LobbyActions.patchPlayers([data]));
          break;
        case 'RENT':
          dispatch(LobbyActions.patchPlayers(data));
          break;
        case 'RESET':
          syncGameStates(dispatch);
          break;
        case 'START':
          dispatch(LobbyActions.patchPlayers([data]));
          break;
        case 'TURN':
          dispatch(LobbyActions.patchPlayers(data));
          break;
        default:
          console.warn(`Unsupported update type: ${type}`);
      }
      dispatch(ActivityActions.addActivity(activity));
      api.open({ message: parseActivity(activity) });
    };
    updates.onerror = console.error;

    // Dashboard component's unmount event
    window.addEventListener('beforeunload', alertUser);
    return () => {
      // Closing Event Stream for the current user
      updates.onmessage = null;
      updates.onerror = null;
      updates.close();

      // Removing listener if user logouts
      window.removeEventListener('beforeunload', alertUser);
    };
  }, [dispatch, navigate, username, api]);

  return (
    <>
      {contextHolder}
      <Row className="strategists-dashboard strategists-wallpaper">
        <Col className="strategists-glossy" flex="30%">
          {Navigation(type, dispatch)}
          {type === 'admin' ? AdminPanel() : PlayerPanel()}
        </Col>
        <Col flex="70%">
          <Map />
        </Col>
      </Row>
    </>
  );
};

const Navigation = (type: 'admin' | 'player', dispatch: Dispatch<any>) => {
  const { state, players } = useSelector((state: State) => state.lobby);
  const [showResetModal, setShowResetModal] = useState(false);

  const start = async () => {
    if (state === 'ACTIVE') return;
    await axios.post('/api/game');
    dispatch(LobbyActions.setState('ACTIVE'));
  };

  const reset = () => {
    if (state === 'LOBBY') return;
    setShowResetModal(true);
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
            onClick={() => dispatch(UserActions.unsetUser())}
          />
        </Tooltip>
        <Logo />
      </header>
      {type === 'admin' ? (
        <Tooltip
          title={
            !players.length
              ? 'Add players to start The Strategists!'
              : state === 'ACTIVE'
              ? 'Reset The Strategists!'
              : 'Start The Strategists!'
          }
        >
          <Button
            type="primary"
            htmlType="submit"
            onClick={() => (state === 'ACTIVE' ? reset() : start())}
          >
            {state === 'LOBBY' ? <PlayCircleFilled /> : <StopFilled />}
          </Button>
        </Tooltip>
      ) : null}
      <ResetModal
        open={showResetModal}
        onCancel={() => setShowResetModal(false)}
      />
    </nav>
  );
};

const AdminPanel = () => {
  return (
    <Tabs
      centered
      defaultActiveKey="1"
      size="large"
      items={[
        {
          key: '1',
          label: `Lobby`,
          children: <Lobby />,
          className: 'strategists-tab-body strategists-lobby',
        },
        {
          key: '2',
          label: `Timeline`,
          children: <ActivityTimeline />,
          className: 'strategists-tab-body',
        },
        {
          key: '3',
          label: `Events`,
          children: `Content of Tab Events`,
        },
      ]}
    />
  );
};

const PlayerPanel = () => {
  return (
    <div className="strategists-player-panel">
      <Stats />
      <ActivityTimeline />
      <Actions />
    </div>
  );
};
