import {
  AuditOutlined,
  EllipsisOutlined,
  LoadingOutlined,
  StepForwardOutlined,
  StockOutlined,
  StopOutlined,
} from '@ant-design/icons';
import { Button, Dropdown, Space } from 'antd';
import { useState } from 'react';
import { useSelector } from 'react-redux';
import { Player, State } from '../redux';
import { InvestmentStrategy } from '../utils';
import { PlayerInvestModal } from '.';
import axios from 'axios';

export const Actions = () => {
  const { players, lands } = useSelector((state: State) => state.lobby);
  const { gameCode, playerId } = useSelector((state: State) => state.login);
  const [showModal, setShowModal] = useState(false);

  // Finding user in lobby's players
  const player = players.find((p) => p.id === playerId);
  const turnPlayer = player?.turn ? player : players.find((p) => p.turn);
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
  return (
    <>
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
          onClick={() => axios.put(`/api/games/${gameCode}/turn`)}
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
