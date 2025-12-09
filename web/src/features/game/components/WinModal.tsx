import { useState } from 'react';
import { useDispatch } from 'react-redux';
import { Badge, Button, Card, Flex, Modal, Row, Space, Tabs } from 'antd';
import {
  CrownFilled,
  HeartOutlined,
  InfoCircleOutlined,
  LogoutOutlined,
  StopFilled,
} from '@ant-design/icons';
import StrategistsLogo from '@shared/components/StrategistsLogo';
import useGameState from '@game/hooks/useGameState';
import useLoginState from '@login/hooks/useLoginState';
import { loggedOut } from '@login/state';
import PredictionsChart from '@predictions/components/PredictionsChart';
import TrendsChart from '@trends/components/TrendsChart';
import PlayerAvatar from './PlayerAvatar';
import PortfolioChart from './PortfolioChart';
import ResetModal from './ResetModal';
import WinnerPlayerCard from './WinnerPlayerCard';

function WinModal() {
  const { gameCode, player } = useLoginState();
  const { winnerPlayer } = useGameState();
  const [showResetModal, setShowResetModal] = useState(false);
  const dispatch = useDispatch();

  if (!gameCode || !winnerPlayer) return null;
  return (
    <>
      <Modal
        className="strategists-modal strategists-win-modal"
        open={!!winnerPlayer}
        onCancel={() => setShowResetModal(false)}
        closable={false}
        maskClosable={false}
        title={
          <Row justify="space-between" align="middle">
            <StrategistsLogo />
            <Space align="center">
              <Badge
                count={<CrownFilled style={{ color: 'goldenrod' }} />}
                offset={[-12, -6]}
              >
                <PlayerAvatar username={winnerPlayer.username} />
              </Badge>
              {winnerPlayer.username}
            </Space>
          </Row>
        }
        footer={
          <Row justify="space-between" align="middle">
            {player?.host ? (
              <>
                <Space>
                  <InfoCircleOutlined />
                  Reset the game to play again.
                </Space>
                <Button
                  type="primary"
                  icon={<StopFilled />}
                  onClick={() => setShowResetModal(true)}
                >
                  Reset
                </Button>
              </>
            ) : (
              <>
                <Space>
                  <HeartOutlined />
                  Thank you for playing The Strategists!
                </Space>
                <Button
                  type="primary"
                  icon={<LogoutOutlined />}
                  onClick={() => dispatch(loggedOut())}
                >
                  Logout
                </Button>
              </>
            )}
          </Row>
        }
      >
        <Flex orientation="vertical" gap="large">
          <WinnerPlayerCard />
          <Card className="strategists-win-modal__tabs_card">
            <Tabs
              centered
              defaultActiveKey="1"
              size="large"
              items={[
                {
                  key: '1',
                  label: 'Trends',
                  children: (
                    <TrendsChart perspective="player" id={winnerPlayer.id} />
                  ),
                },
                {
                  key: '2',
                  label: 'Portfolio',
                  children: (
                    <PortfolioChart
                      perspective="player"
                      playerLands={winnerPlayer.lands}
                    />
                  ),
                },
                {
                  key: '3',
                  label: 'Predictions',
                  children: <PredictionsChart player={winnerPlayer} />,
                },
              ]}
            />
          </Card>
        </Flex>
      </Modal>
      <ResetModal
        open={showResetModal}
        gameCode={gameCode}
        onCancel={() => setShowResetModal(false)}
      />
    </>
  );
}

export default WinModal;
