import {
  AuditOutlined,
  EllipsisOutlined,
  LoadingOutlined,
  StepForwardOutlined,
  StockOutlined,
  StopOutlined,
} from '@ant-design/icons';
import { Button, Dropdown, Space, notification } from 'antd';
import { useState } from 'react';
import { Player } from '../features/game/slice';
import { useGame, useLogin } from '../hooks';
import { InvestmentStrategy } from '../utils';
import { PlayerInvestModal } from '.';
import axios from 'axios';

export const Actions = () => {
  const { gameCode, player } = useLogin();
  const { turnPlayer, lands } = useGame();
  const [showModal, setShowModal] = useState(false);

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

  return (
    <>
      <PlayerInvestModal
        open={showModal}
        gameCode={gameCode}
        player={player}
        land={land}
        title={title}
        onCancel={() => setShowModal(false)}
      />
      <div className="strategists-actions">
        {player?.state === 'BANKRUPT' ? (
          <BankruptPrompt />
        ) : !player.turn ? (
          <WaitingPrompt turnPlayer={turnPlayer} />
        ) : (
          <ActionButtons
            gameCode={gameCode}
            title={title}
            onInvestClick={() => setShowModal(true)}
            isInvestmentDisabled={!strategy.feasible}
          />
        )}
      </div>
    </>
  );
};

/**
 * -----  BANKRUPT PROMPT BELOW  -----
 */

const BankruptPrompt = () => (
  <Button disabled size="large" type="primary" icon={<AuditOutlined />}>
    You are declared bankrupt!
  </Button>
);

/**
 * -----  WAITING PROMPT BELOW  -----
 */

interface WaitingPromptProps {
  turnPlayer?: Player;
}

const WaitingPrompt = (props: WaitingPromptProps) => {
  const { turnPlayer } = props;
  return (
    <Button disabled size="large" type="primary" icon={<LoadingOutlined />}>
      {turnPlayer
        ? `${turnPlayer.username}'s turn to invest`
        : 'The Strategists not started!'}
    </Button>
  );
};

/**
 * -----  ACTION BUTTONS BELOW  -----
 */

interface ActionButtonsProps {
  gameCode: string;
  title: string;
  onInvestClick: () => void;
  isInvestmentDisabled: boolean;
}

const ActionButtons = (props: ActionButtonsProps) => {
  const { gameCode, title, onInvestClick, isInvestmentDisabled } = props;
  const [skipping, setSkipping] = useState(false);
  const [api, contextHolder] = notification.useNotification();

  const onSkip = async () => {
    try {
      setSkipping(true);
      await axios.put(`/api/games/${gameCode}/turn`);
    } catch (error) {
      console.error(error);
      api.error({
        message: 'Something went wrong!',
        description: 'Please refresh the page and try again.',
      });
    } finally {
      setSkipping(false);
    }
  };

  return (
    <>
      {contextHolder}
      <Space.Compact size="large">
        <Button
          type="primary"
          disabled={isInvestmentDisabled}
          icon={isInvestmentDisabled ? <StopOutlined /> : <StockOutlined />}
          onClick={onInvestClick}
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
    </>
  );
};
