import { useState } from 'react';
import { useDispatch } from 'react-redux';
import { googleLogout } from '@react-oauth/google';
import { Button, Tooltip } from 'antd';
import {
  LogoutOutlined,
  PlayCircleFilled,
  StopFilled,
} from '@ant-design/icons';
import { loggedOut } from '@login/state';
import StrategistsLogo from '@shared/components/StrategistsLogo';
import { PlayerPanelProps } from './PlayerPanel';
import ResetModal from './ResetModal';
import axios from 'axios';

interface NavigationBarProps extends PlayerPanelProps {
  // No additional fields needed
}

const NavigationBar = (props: NavigationBarProps) => {
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
        <StrategistsLogo />
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

export default NavigationBar;
