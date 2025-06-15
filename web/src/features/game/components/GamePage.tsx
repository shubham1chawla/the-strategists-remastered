import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { Col, Row } from 'antd';
import useGame from '@game/hooks/useGame';
import CytoscapeProvider from '@game/providers/cytoscapeProvider';
import syncGameStates from '@game/utils/syncGameStates';
import useLogin from '@login/hooks/useLogin';
import { loggedOut } from '@login/state';
import MapPanel from './MapPanel';
import PlayerPanel from './PlayerPanel';
import TurnModal from './TurnModal';
import UpdateInterceptor from './UpdateInterceptor';
import WinModal from './WinModal';

const GamePage = () => {
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
      <UpdateInterceptor />
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
          <CytoscapeProvider>
            <MapPanel />
          </CytoscapeProvider>
        </Col>
      </Row>
      <WinModal />
    </>
  );
};

export default GamePage;
