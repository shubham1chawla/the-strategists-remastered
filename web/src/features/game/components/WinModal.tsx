import { useState } from 'react';
import { useDispatch } from 'react-redux';
import { Button, Modal, Row, Space, Tabs, Tag } from 'antd';
import {
  CrownOutlined,
  HeartOutlined,
  InfoCircleOutlined,
  LogoutOutlined,
  StopFilled,
} from '@ant-design/icons';
import useLogin from '@login/hooks/useLogin';
import { loggedOut } from '@login/state';
import useGame from '@game/hooks/useGame';
import PredictionsChart from '@predictions/components/PredictionsChart';
import ConfettiBackdrop from '@shared/components/ConfettiBackdrop';
import StrategistsLogo from '@shared/components/StrategistsLogo';
import TrendsChart from '@trends/components/TrendsChart';
import PlayerStats from './PlayerStats';
import PortfolioChart from './PortfolioChart';
import ResetModal from './ResetModal';

const WinModal = () => {
  const { gameCode, player } = useLogin();
  const { winnerPlayer } = useGame();
  const [showResetModal, setShowResetModal] = useState(false);
  const dispatch = useDispatch();

  if (!gameCode || !winnerPlayer) return null;
  return (
    <>
      <ConfettiBackdrop type="multiple" />
      <Modal
        open={!!winnerPlayer}
        onCancel={() => setShowResetModal(false)}
        closable={false}
        maskClosable={false}
        title={
          <Row justify="space-between" align="middle">
            <StrategistsLogo />
            <Tag icon={<CrownOutlined />}>Winner</Tag>
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
        <PlayerStats player={winnerPlayer} winner />
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
      </Modal>
      {showResetModal && (
        <ResetModal
          open={showResetModal}
          gameCode={gameCode}
          onCancel={() => setShowResetModal(false)}
        />
      )}
    </>
  );
};

export default WinModal;
