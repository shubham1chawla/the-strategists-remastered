import { Col, Row } from 'antd';
import CytoscapeProvider from '@game/providers/cytoscapeProvider';
import GameWrapper from './GameWrapper';
import MapPanel from './MapPanel';
import PlayerPanel from './PlayerPanel';
import SkipPlayerTimer from './SkipPlayerTimer';
import TurnModal from './TurnModal';
import WinModal from './WinModal';
import WinnerConfettiBackdrop from './WinnerConfettiBackdrop';

function GamePage() {
  return (
    <GameWrapper>
      <TurnModal />
      <Row className="strategists-dashboard">
        <Col flex="30%">
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
      <SkipPlayerTimer />
    </GameWrapper>
  );
}

export default GamePage;
