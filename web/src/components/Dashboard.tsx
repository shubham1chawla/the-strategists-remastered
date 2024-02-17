import { Dispatch, useEffect, useMemo, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { AnyAction } from 'redux';
import { useNavigate } from 'react-router-dom';
import { Button, Col, Row, Tabs, Tooltip, notification } from 'antd';
import { googleLogout } from '@react-oauth/google';
import {
  DisconnectOutlined,
  LogoutOutlined,
  PlayCircleFilled,
  StopFilled,
} from '@ant-design/icons';
import {
  Actions,
  ActivityTimeline,
  Logo,
  Lobby,
  Map,
  PlayerStats,
  ResetModal,
  WinModal,
} from '.';
import {
  Activity,
  ActivityActions,
  LobbyActions,
  Player,
  State,
  TrendActions,
  UpdateType,
  UserActions,
} from '../redux';
import { parseActivity } from '../utils';
import axios from 'axios';

/**
 * -----  UTILITIES DEFINED BELOW  -----
 */

const syncGameStates = (
  gameId: number,
  dispatch: Dispatch<AnyAction>
): void => {
  // Defining all the GET calls to sync with server
  const prefix = `/api/games/${gameId}`;
  [
    {
      url: `${prefix}/players`,
      action: LobbyActions.setPlayers,
    },
    {
      url: `${prefix}/activities`,
      action: ActivityActions.setActivities,
    },
    {
      url: `${prefix}/lands`,
      action: LobbyActions.setLands,
    },
    {
      url: `${prefix}/state`,
      action: LobbyActions.setState,
    },
    {
      url: `${prefix}/trends`,
      action: TrendActions.setTrends,
    },
  ].forEach(({ url, action }) =>
    axios
      .get(url)
      .then(({ data }) => dispatch(action(data)))
      .catch(console.error)
  );
};

/**
 * -----  DASHBOARD COMPONENT BELOW  -----
 */

export const Dashboard = () => {
  const lobby = useSelector((state: State) => state.lobby);
  const user = useSelector((state: State) => state.user);
  const { gameId, username, type } = user;
  const { players } = lobby;

  const dispatch = useDispatch();
  const navigate = useNavigate();

  const alertUser = (event: any) => {
    event.preventDefault();
    return (event.returnValue = '');
  };

  // Checking if user is logged-in
  useEffect(() => {
    if (!gameId) {
      navigate('/login');
      return;
    }

    // Syncing game's state
    syncGameStates(gameId, dispatch);

    // Dashboard component's unmount event
    window.addEventListener('beforeunload', alertUser);
    return () => {
      // Removing listener if user logouts
      window.removeEventListener('beforeunload', alertUser);
    };
  }, [dispatch, navigate, gameId]);

  // Determining player
  const player = players.find((player) => player.username === username);

  return (
    <>
      <Update />
      <Row className="strategists-dashboard strategists-wallpaper">
        <Col className="strategists-glossy" flex="30%">
          <Navigation />
          {type === 'ADMIN' ? (
            <AdminPanel />
          ) : player ? (
            <PlayerPanel player={player} />
          ) : null}
        </Col>
        <Col flex="70%">
          <Map />
        </Col>
      </Row>
      <WinModal />
    </>
  );
};

/**
 * -----  UPDATE COMPONENT BELOW  -----
 */

interface UpdatePayload {
  type: UpdateType;
  activity?: Activity;
  payload: any;
}

const Update = () => {
  const activity = useSelector((state: State) => state.activity);
  const user = useSelector((state: State) => state.user);
  const { subscribedTypes } = activity;
  const { gameId, username } = user;
  const [api, contextHolder] = notification.useNotification();

  const dispatch = useDispatch();

  /**
   * This useMemo ensures that we'll change the event source's instance
   * only when the username changes.
   */
  const updates = useMemo(() => {
    return !username
      ? null
      : new EventSource(`/api/games/${gameId}/sse?username=${username}`);
  }, [gameId, username]);

  /**
   * This useEffect will only update the event source's onmessage hook.
   */
  useEffect(() => {
    if (!updates || !gameId) return;

    // Setting up onerror startegy for the event source
    updates.onerror = (error) => {
      console.error(error);

      // Preventing reconnection using the same instance.
      updates.close();

      // Showing notification to the user, urging them to refresh the page.
      api.error({
        icon: <DisconnectOutlined />,
        message: 'Disconnected!',
        description:
          'We lost the connection to our servers. Refresh the page to reconnect!',
        duration: 0,
        onClose: () => window.location.reload(),
      });
    };

    // Setting up on message strategy for the event source
    updates.onmessage = (message: MessageEvent<any>) => {
      const { type, payload, activity }: UpdatePayload = JSON.parse(
        message.data
      );
      switch (type) {
        case 'BANKRUPTCY': {
          const { lands, players } = payload;
          dispatch(LobbyActions.patchLands(lands));
          dispatch(LobbyActions.patchPlayers(players));

          // Skipping turn if current player declared bankruptcy
          for (const p of players as Player[]) {
            if (p.turn && p.username === username && p.state === 'BANKRUPT') {
              axios.put(`/api/games/${gameId}/turn`);
              break;
            }
          }
          break;
        }
        case 'END':
          // Do nothing
          break;
        case 'INVEST': {
          const { land, players } = payload;
          dispatch(LobbyActions.patchLands([land]));
          dispatch(LobbyActions.patchPlayers(players));
          break;
        }
        case 'INVITE':
          dispatch(LobbyActions.addPlayer(payload));
          break;
        case 'JOIN':
          dispatch(LobbyActions.patchPlayers([payload]));
          break;
        case 'KICK':
          dispatch(LobbyActions.kickPlayer(payload));
          break;
        case 'MOVE':
          dispatch(LobbyActions.patchPlayers([payload]));
          break;
        case 'PING':
        case 'PREDICTION':
          // Do nothing
          break;
        case 'RENT':
          dispatch(LobbyActions.patchPlayers(payload));
          break;
        case 'RESET':
          /**
           * Unknown issue here. Some clients refresh game's state but some don't (rarely).
           * Adding the setTimeout seems to work here but root cause is still unknown.
           */
          setTimeout(() => syncGameStates(gameId, dispatch));
          break;
        case 'START':
          dispatch(LobbyActions.patchPlayers([payload]));
          dispatch(LobbyActions.setState('ACTIVE'));
          break;
        case 'TREND':
          dispatch(TrendActions.addTrends(payload));
          break;
        case 'TURN':
          dispatch(LobbyActions.patchPlayers(payload));
          break;
        default:
          console.warn(`Unsupported update type: ${type}`);
      }
      if (!activity) return;
      dispatch(ActivityActions.addActivity(activity));
      if (subscribedTypes.includes(type)) {
        api.open({ message: parseActivity(activity) });
      }
    };
  }, [api, dispatch, subscribedTypes, updates, username, gameId]);

  /**
   * This useEffect will close the event source for the
   * current user if they decide to logout or closes the tab.
   */
  useEffect(() => {
    return () => {
      if (!updates) {
        return;
      }
      updates.onmessage = null;
      updates.onerror = null;
      updates.close();
    };
  }, [updates]);

  return <>{contextHolder}</>;
};

/**
 * -----  NAVIGATION COMPONENT BELOW  -----
 */

const Navigation = () => {
  const lobby = useSelector((state: State) => state.lobby);
  const user = useSelector((state: State) => state.user);
  const { gameId, type } = user;
  const { state, players } = lobby;

  const dispatch = useDispatch();

  const [showResetModal, setShowResetModal] = useState(false);

  const start = () => {
    if (state === 'ACTIVE') return;
    axios.put(`/api/games/${gameId}/start`);
  };

  const reset = () => {
    if (state === 'LOBBY') return;
    setShowResetModal(true);
  };

  const logout = () => {
    googleLogout();
    dispatch(UserActions.unsetUser());
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
            onClick={logout}
          />
        </Tooltip>
        <Logo />
      </header>
      {type === 'ADMIN' ? (
        <Tooltip
          title={
            !players.length
              ? 'Add players to start The Strategists!'
              : !!players.find((p) => p.state === 'INVITED')
              ? 'All players must accept the invite!'
              : state === 'ACTIVE'
              ? 'Reset The Strategists!'
              : 'Start The Strategists!'
          }
        >
          <Button
            type="primary"
            htmlType="submit"
            disabled={
              state === 'LOBBY' &&
              (!players.length || !!players.find((p) => p.state === 'INVITED'))
            }
            onClick={() => (state === 'ACTIVE' ? reset() : start())}
          >
            {state === 'LOBBY' ? <PlayCircleFilled /> : <StopFilled />}
          </Button>
        </Tooltip>
      ) : null}
      <ResetModal
        open={showResetModal}
        gameId={gameId || -1}
        onCancel={() => setShowResetModal(false)}
      />
    </nav>
  );
};

/**
 * -----  ADMIN PANEL COMPONENT BELOW  -----
 */

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
      ]}
    />
  );
};

/**
 * -----  PLAYER PANEL COMPONENT BELOW  -----
 */

interface PlayerPanelProps {
  player: Player;
}

const PlayerPanel = (props: PlayerPanelProps) => {
  const { player } = props;
  return (
    <div className="strategists-player-panel">
      <PlayerStats player={player} />
      <ActivityTimeline />
      <Actions />
    </div>
  );
};
