import { Col, Row } from 'antd';
import CytoscapeProvider from '@game/providers/cytoscapeProvider';
import GameWrapper from './GameWrapper';
import MapPanel from './MapPanel';
import PlayerPanel from './PlayerPanel';
import TurnModal from './TurnModal';
import WinModal from './WinModal';
import WinnerConfettiBackdrop from './WinnerConfettiBackdrop';

function GamePage() {
  return (
    <GameWrapper>
      <TurnModal />
      <Row className="strategists-dashboard strategists-wallpaper">
        <Col
          className="strategists-dashboard__panel strategists-glossy"
          flex="30%"
        >
          <PlayerPanel />
        </Col>
        <Col flex="70%">
          <CytoscapeProvider>
            <MapPanel />
          </CytoscapeProvider>
        </Col>
      </Row>
      <WinnerConfettiBackdrop />
      <WinModal />
    </GameWrapper>
  );
}

export default GamePage;
