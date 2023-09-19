import {
  AuditOutlined,
  EllipsisOutlined,
  LoadingOutlined,
  StepForwardOutlined,
  StockOutlined,
  StopOutlined,
} from '@ant-design/icons';
import { Button, Dropdown, Space } from 'antd';
import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { Player, State } from '../redux';
import { PlayerInvestModal } from '.';
import axios from 'axios';

export const Actions = () => {
  const { players, lands } = useSelector((state: State) => state.lobby);
  const { username } = useSelector((state: State) => state.user);

  const [isWaiting, setWaiting] = useState(true);
  const [isInvestmentDisabled, setInvestmentDisabled] = useState(true);
  const [investText, setInvestText] = useState('');
  const [showModal, setShowModal] = useState(false);

  // Finding user in lobby's players
  const player = players.find((p) => p.username === username);
  const turnPlayer = players.find((p) => p.turn);
  const land = player ? lands[player.index] : undefined;

  useEffect(() => {
    if (!player || !land) {
      return;
    }

    // Checking if investment is allowed
    if (!land.marketValue || player.cash < 0.01 * land.marketValue) {
      setInvestText(
        !land.marketValue
          ? `Cannot invest in ${land?.name}`
          : `Not enough cash to invest!`
      );
      setInvestmentDisabled(true);
    } else {
      setInvestText(`Invest in ${land?.name}`);
      setInvestmentDisabled(false);
    }
    setWaiting(!player.turn);
  }, [player, land]);

  return (
    <>
      <PlayerInvestModal
        open={showModal}
        player={player}
        land={land}
        investText={investText}
        onCancel={() => setShowModal(false)}
      />
      <div className="strategists-actions">
        {player?.state === 'BANKRUPT' ? (
          <BankruptPrompt />
        ) : isWaiting ? (
          <WaitingPrompt turnPlayer={turnPlayer} />
        ) : (
          <ActionButtons
            investText={investText}
            onInvestClick={() => setShowModal(true)}
            isInvestmentDisabled={isInvestmentDisabled}
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
  investText: string;
  onInvestClick: () => void;
  isInvestmentDisabled: boolean;
}

const ActionButtons = (props: ActionButtonsProps) => {
  const { investText, onInvestClick, isInvestmentDisabled } = props;
  return (
    <>
      <Space.Compact size="large">
        <Button
          type="primary"
          disabled={isInvestmentDisabled}
          icon={isInvestmentDisabled ? <StopOutlined /> : <StockOutlined />}
          onClick={onInvestClick}
        >
          {investText}
        </Button>
        <Button
          icon={<StepForwardOutlined />}
          onClick={() => axios.put('/api/game')}
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
