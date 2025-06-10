import { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { Badge, Button, Col, Row, Space, Tabs, TabsProps, Tooltip } from 'antd';
import { googleLogout } from '@react-oauth/google';
import {
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
  Update,
  Advices,
  TurnModal,
} from '.';
import { Player } from '../features/game/slice';
import { loggedOut } from '../features/login/slice';
import { useAdvices, useGame, useLogin } from '../hooks';
import { syncGameStates } from '../utils';
import axios from 'axios';

/**
 * -----  DASHBOARD COMPONENT BELOW  -----
 */

export const Dashboard = () => {
  const { gameCode, player } = useLogin();
  const { state, players, minPlayersCount, maxPlayersCount } = useGame();

  const dispatch = useDispatch();
  const navigate = useNavigate();

  const alertUser = (event: any) => {
    event.preventDefault();
    return (event.returnValue = '');
  };

  // Checking if user is logged-in
  useEffect(() => {
    if (!gameCode) {
      navigate('/login');
      return;
    }

    // Syncing game's state
    syncGameStates(gameCode, dispatch).catch((error) => {
      console.error(error);
      dispatch(loggedOut());
    });

    // Dashboard component's unmount event
    window.addEventListener('beforeunload', alertUser);
    return () => {
      // Removing listener if user logouts
      window.removeEventListener('beforeunload', alertUser);
    };
  }, [dispatch, navigate, gameCode]);

  // Determining player
  if (!gameCode || !player) return null;

  return (
    <>
      <Update />
      <TurnModal />
      <Row className="strategists-dashboard strategists-wallpaper">
        <Col
          className="strategists-dashboard__panel strategists-glossy"
          flex="30%"
        >
          <PlayerPanel
            gameCode={gameCode}
            player={player}
            state={state}
            players={players}
            minPlayersCount={minPlayersCount}
            maxPlayersCount={maxPlayersCount}
          />
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
 * -----  PLAYER PANEL COMPONENT BELOW  -----
 */

interface PlayerPanelProps {
  gameCode: string;
  player: Player;
  state: 'LOBBY' | 'ACTIVE';
  players: Player[];
  minPlayersCount: number;
  maxPlayersCount: number;
}

type PlayerPanelTabKey = 'LOBBY' | 'TIMELINE' | 'ADVICE';

const PlayerPanel = (props: PlayerPanelProps) => {
  const { player, state } = props;
  const [activeKey, setActiveKey] = useState<PlayerPanelTabKey>('LOBBY');
  const { playerAdvices, unreadCount, markAdvicesRead } = useAdvices();

  // Switching tabs when game's state changes
  useEffect(() => {
    setActiveKey(state === 'ACTIVE' ? 'TIMELINE' : 'LOBBY');
  }, [state]);

  // Creating tabs
  const items: TabsProps['items'] = [
    {
      key: 'LOBBY',
      label: 'Lobby',
      children: <Lobby />,
    },
    {
      key: 'TIMELINE',
      label: 'Timeline',
      children: <ActivityTimeline />,
    },
  ];

  // Adding Advice tab if the feature is enabled
  if (playerAdvices.length) {
    items.push({
      key: 'ADVICE',
      label: (
        <Space align="center">
          <span>Advices</span>
          <Badge count={unreadCount} offset={[0, -2]} status="default" />
        </Space>
      ),
      children: <Advices />,
    });
  }

  const onTabChange = (key: string) => {
    setActiveKey((prev) => {
      if (prev === 'ADVICE') {
        markAdvicesRead();
      }
      return key as PlayerPanelTabKey;
    });
  };

  return (
    <>
      <Navigation {...props} />
      <PlayerStats player={player} showRemainingSkipsCount />
      <Tabs
        centered
        defaultActiveKey="LOBBY"
        activeKey={activeKey}
        onChange={onTabChange}
        size="large"
        items={items}
      />
      <Actions />
    </>
  );
};

/**
 * -----  NAVIGATION COMPONENT BELOW  -----
 */

interface NavigationProps extends PlayerPanelProps {
  // No additional fields needed
}

const Navigation = (props: NavigationProps) => {
  const { gameCode, player, state, players, minPlayersCount, maxPlayersCount } =
    props;
  const [showResetModal, setShowResetModal] = useState(false);
  const dispatch = useDispatch();

  const start = () => {
    if (state === 'ACTIVE') return;
    axios.put(`/api/games/${gameCode}/start`);
  };

  const reset = () => {
    if (state === 'LOBBY') return;
    setShowResetModal(true);
  };

  const logout = () => {
    googleLogout();
    dispatch(loggedOut());
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
      {player?.host && (
        <Tooltip
          title={
            players.length < minPlayersCount
              ? `At least ${minPlayersCount} players required to start The Strategists!`
              : players.length > maxPlayersCount
              ? `At most ${maxPlayersCount} players required to start The Strategists!`
              : state === 'ACTIVE'
              ? 'Reset this session of The Strategists!'
              : 'Start this session of The Strategists!'
          }
        >
          <Button
            type="primary"
            htmlType="submit"
            disabled={
              state === 'LOBBY' &&
              (players.length < minPlayersCount ||
                players.length > maxPlayersCount)
            }
            onClick={() => (state === 'ACTIVE' ? reset() : start())}
          >
            {state === 'LOBBY' ? <PlayCircleFilled /> : <StopFilled />}
          </Button>
        </Tooltip>
      )}
      <ResetModal
        open={showResetModal}
        gameCode={gameCode}
        onCancel={() => setShowResetModal(false)}
      />
    </nav>
  );
};
