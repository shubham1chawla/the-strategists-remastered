import { useState } from 'react';
import { useDispatch } from 'react-redux';
import { Button, Tooltip } from 'antd';
import {
  LogoutOutlined,
  PlayCircleFilled,
  StopFilled,
} from '@ant-design/icons';
import axios from 'axios';
import { googleLogout } from '@react-oauth/google';
import StrategistsLogo from '@shared/components/StrategistsLogo';
import useGame from '@game/hooks/useGame';
import useLogin from '@login/hooks/useLogin';
import { loggedOut } from '@login/state';
import ResetModal from './ResetModal';

function NavigationBar() {
  const { gameCode, player } = useLogin();
  const { state, players, minPlayersCount, maxPlayersCount } = useGame();
  const [showResetModal, setShowResetModal] = useState(false);
  const dispatch = useDispatch();

  // Validations
  if (!gameCode) {
    return null;
  }

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
}

export default NavigationBar;
