import {
  EllipsisOutlined,
  LoadingOutlined,
  StepForwardOutlined,
  StockOutlined,
} from '@ant-design/icons';
import { Button, Dropdown, Space } from 'antd';
import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { State } from '../redux';
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
        {disabled ? (
          <Button
            disabled
            size="large"
            type="primary"
            icon={<LoadingOutlined />}
          >
            Waiting for your turn
          </Button>
        ) : (
          <>
            <Space.Compact size="large">
              <Button
                type="primary"
                icon={<StockOutlined />}
                onClick={() => setShowModal(true)}
              >
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
        )}
      </div>
    </>
  );
};
