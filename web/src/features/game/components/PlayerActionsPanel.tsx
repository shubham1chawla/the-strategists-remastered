import { useState } from 'react';
import { Button, Dropdown, Space } from 'antd';
import {
  AuditOutlined,
  EllipsisOutlined,
  LoadingOutlined,
  StepForwardOutlined,
  StockOutlined,
  StopOutlined,
} from '@ant-design/icons';
import axios from 'axios';
import useNotifications from '@shared/hooks/useNotifications';
import useGameState from '@game/hooks/useGameState';
import InvestmentStrategy from '@game/utils/InvestmentStrategy';
import useLoginState from '@login/hooks/useLoginState';
import PlayerInvestModal from './PlayerInvestModal';

function PlayerActionsPanel() {
  const { gameCode, player } = useLoginState();
  const { turnPlayer, lands } = useGameState();
  const { errorNotification } = useNotifications();
  const [showModal, setShowModal] = useState(false);
  const [skipping, setSkipping] = useState(false);

  // Determining player's current land
  const land = player ? lands[player.index] : undefined;

  if (!player || !land || !gameCode) {
    return null;
  }

  // Defining investment strategy
  const strategy = new InvestmentStrategy(player, land, 1);
  let title = '';

  // Checking if investment is allowed
  if (!strategy.feasible) {
    title = !land.marketValue
      ? `Cannot invest in ${land?.name}`
      : land.totalOwnership >= 100
        ? 'No shares available!'
        : `Not enough cash to invest!`;
  } else {
    title = `Invest in ${land?.name}`;
  }

  const onSkip = async () => {
    try {
      setSkipping(true);
      await axios.put(`/api/games/${gameCode}/turn`);
    } catch (error) {
      errorNotification({
        message: 'Something went wrong!',
        description: 'Please refresh the page and try again.',
      });
    } finally {
      setSkipping(false);
    }
  };

  return (
    <>
      <PlayerInvestModal
        open={showModal}
        onCancel={() => setShowModal(false)}
      />
      <div className="strategists-actions">
        {player?.state === 'BANKRUPT' ? (
          <Button disabled size="large" type="primary" icon={<AuditOutlined />}>
            You are declared bankrupt!
          </Button>
        ) : !player.turn ? (
          <Button
            disabled
            size="large"
            type="primary"
            icon={<LoadingOutlined />}
          >
            {turnPlayer
              ? `${turnPlayer.username}'s turn to invest`
              : 'The Strategists not started!'}
          </Button>
        ) : (
          <Space.Compact size="large">
            <Button
              type="primary"
              disabled={!strategy.feasible}
              icon={!strategy.feasible ? <StopOutlined /> : <StockOutlined />}
              onClick={() => setShowModal(true)}
            >
              {title}
            </Button>
            <Button
              icon={<StepForwardOutlined />}
              disabled={skipping}
              onClick={onSkip}
              loading={skipping}
            >
              Skip
            </Button>
            <Dropdown
              menu={{
                items: [
                  {
                    key: '1',
                    label: 'Apply Cheat',
                  },
                ],
              }}
              trigger={['click']}
            >
              <Button icon={<EllipsisOutlined />} />
            </Dropdown>
          </Space.Compact>
        )}
      </div>
    </>
  );
}

export default PlayerActionsPanel;
