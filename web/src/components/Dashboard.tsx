import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { Button, Col, Row, Tabs, Tooltip } from 'antd';
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
} from '.';
import { LoginActions, Player, State } from '../redux';
import { syncGameStates } from '../utils';
import axios from 'axios';

/**
 * -----  DASHBOARD COMPONENT BELOW  -----
 */

export const Dashboard = () => {
  const { state, players } = useSelector((state: State) => state.lobby);
  const { gameCode, playerId } = useSelector((state: State) => state.login);

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
    syncGameStates(gameCode, dispatch);

    // Dashboard component's unmount event
    window.addEventListener('beforeunload', alertUser);
    return () => {
      // Removing listener if user logouts
      window.removeEventListener('beforeunload', alertUser);
    };
  }, [dispatch, navigate, gameCode]);

  // Determining player
  const player = players.find((p) => p.id === playerId);
  if (!gameCode || !player) return null;

  return (
    <>
      <Update />
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
}

const PlayerPanel = (props: PlayerPanelProps) => {
  const { player, state } = props;
  const [activeKey, setActiveKey] = useState(state);

  // Switching tabs when game's state changes
  useEffect(() => {
    setActiveKey(state);
  }, [state]);

  return (
    <>
      <Navigation {...props} />
      <PlayerStats player={player} showRemainingSkipsCount />
      <Tabs
        centered
        defaultActiveKey="LOBBY"
        activeKey={activeKey}
        onChange={(key) => setActiveKey(key as 'LOBBY' | 'ACTIVE')}
        size="large"
        items={[
          {
            key: 'LOBBY',
            label: `Lobby`,
            children: <Lobby />,
          },
          {
            key: 'ACTIVE',
            label: `Timeline`,
            children: <ActivityTimeline />,
          },
        ]}
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
  const { gameCode, player, state, players } = props;
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
    dispatch(LoginActions.logout());
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
      {player?.host ? (
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
            disabled={state === 'LOBBY' && !players.length}
            onClick={() => (state === 'ACTIVE' ? reset() : start())}
          >
            {state === 'LOBBY' ? <PlayCircleFilled /> : <StopFilled />}
          </Button>
        </Tooltip>
      ) : null}
      <ResetModal
        open={showResetModal}
        gameCode={gameCode}
        onCancel={() => setShowResetModal(false)}
      />
    </nav>
  );
};
