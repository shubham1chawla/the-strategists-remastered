import {
  AuditOutlined,
  EllipsisOutlined,
  LoadingOutlined,
  StepForwardOutlined,
  StockOutlined,
} from '@ant-design/icons';
import { Button, Dropdown, Space } from 'antd';
import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { Player, State } from '../redux';
import { InvestModal } from '.';
import axios from 'axios';

export const Actions = () => {
  const { players, lands } = useSelector((state: State) => state.lobby);
  const { username } = useSelector((state: State) => state.user);
  const [disabled, setDisabled] = useState(true);
  const [investText, setInvestText] = useState('');
  const [showModal, setShowModal] = useState(false);

  // finding user in lobby's players
  const player = players.find((p) => p.username === username);
  const turnPlayer = players.find((p) => p.turn);
  const land = player ? lands[player.index] : undefined;

  useEffect(() => {
    setDisabled(!player?.turn);
    setInvestText(`Invest in ${land?.name}`);
  }, [player, land]);

  return (
    <>
      <InvestModal
        open={showModal}
        player={player}
        land={land}
        investText={investText}
        onCancel={() => setShowModal(false)}
      />
      <div className="strategists-actions">
        {player?.state === 'BANKRUPT'
          ? renderBankurptButton()
          : disabled
          ? renderDisabledButton(turnPlayer)
          : renderActionButtons(investText, () => setShowModal(true))}
      </div>
    </>
  );
};

const renderBankurptButton = () => (
  <Button disabled size="large" type="primary" icon={<AuditOutlined />}>
    You are declared bankrupt!
  </Button>
);

const renderDisabledButton = (turnPlayer?: Player) => (
  <Button disabled size="large" type="primary" icon={<LoadingOutlined />}>
    {turnPlayer
      ? `${turnPlayer.username}'s turn to invest`
      : 'The Strategists not started!'}
  </Button>
);

const renderActionButtons = (investText: string, onClick: () => void) => (
  <>
    <Space.Compact size="large">
      <Button type="primary" icon={<StockOutlined />} onClick={onClick}>
        {investText}
      </Button>
      <Button
        icon={<StepForwardOutlined />}
        onClick={() => axios.put('/api/game/next')}
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
