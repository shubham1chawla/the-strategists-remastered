import { useState } from 'react';
import { Button, Col, Dropdown, Row, Space, Typography } from 'antd';
import {
  AuditOutlined,
  EllipsisOutlined,
  InfoCircleOutlined,
  LoadingOutlined,
  StepForwardOutlined,
  StockOutlined,
  StopOutlined,
} from '@ant-design/icons';
import axios from 'axios';
import useNotifications from '@shared/hooks/useNotifications';
import useGameState from '@game/hooks/useGameState';
import { Player } from '@game/state';
import InvestmentStrategy from '@game/utils/InvestmentStrategy';
import useLoginState from '@login/hooks/useLoginState';
import PlayerInvestModal from './PlayerInvestModal';

function BankruptPrompt() {
  return (
    <Button disabled size="large" type="primary" icon={<AuditOutlined />} block>
      You are declared bankrupt!
    </Button>
  );
}

interface TurnPlayerPromptProps {
  turnPlayer: Player;
}

function TurnPlayerPrompt({ turnPlayer }: TurnPlayerPromptProps) {
  return (
    <Button
      disabled
      size="large"
      type="primary"
      icon={<LoadingOutlined />}
      block
    >
      {turnPlayer.username}&apos;s turn to invest
    </Button>
  );
}

function JoinGamePrompt() {
  const { game } = useGameState();
  return (
    <Row
      className="strategists-actions__join-prompt"
      align="middle"
      justify="center"
    >
      <Space>
        <InfoCircleOutlined />
        <Typography.Text>
          Use code
          <Typography.Text copyable code>
            {game.code}
          </Typography.Text>
          to join The Strategists.
        </Typography.Text>
      </Space>
    </Row>
  );
}

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
        title: 'Something went wrong!',
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
          <BankruptPrompt />
        ) : !turnPlayer ? (
          <JoinGamePrompt />
        ) : !player.turn ? (
          <TurnPlayerPrompt turnPlayer={turnPlayer} />
        ) : (
          <Space.Compact size="large" block>
            <Col flex="60%">
              <Button
                type="primary"
                disabled={!strategy.feasible}
                icon={!strategy.feasible ? <StopOutlined /> : <StockOutlined />}
                onClick={() => setShowModal(true)}
                block
              >
                {title}
              </Button>
            </Col>
            <Button
              icon={<StepForwardOutlined />}
              disabled={skipping}
              onClick={onSkip}
              loading={skipping}
              block
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
