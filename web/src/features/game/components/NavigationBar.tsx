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
import useGameState from '@game/hooks/useGameState';
import useLoginState from '@login/hooks/useLoginState';
import { loggedOut } from '@login/state';
import ResetModal from './ResetModal';

function NavigationBar() {
  const { gameCode, player } = useLoginState();
  const { game, players } = useGameState();
  const [showResetModal, setShowResetModal] = useState(false);
  const dispatch = useDispatch();

  // Validations
  if (!gameCode) {
    return null;
  }

  const start = () => {
    if (game.state === 'ACTIVE') return;
    axios.put(`/api/games/${gameCode}/start`);
  };

  const reset = () => {
    if (game.state === 'LOBBY') return;
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
            players.length < game.minPlayersCount
              ? `At least ${game.minPlayersCount} players required to start The Strategists!`
              : players.length > game.maxPlayersCount
                ? `At most ${game.maxPlayersCount} players required to start The Strategists!`
                : game.state === 'ACTIVE'
                  ? 'Reset this session of The Strategists!'
                  : 'Start this session of The Strategists!'
          }
        >
          <Button
            type="primary"
            htmlType="submit"
            disabled={
              game.state === 'LOBBY' &&
              (players.length < game.minPlayersCount ||
                players.length > game.maxPlayersCount)
            }
            onClick={() => (game.state === 'ACTIVE' ? reset() : start())}
          >
            {game.state === 'LOBBY' ? <PlayCircleFilled /> : <StopFilled />}
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
