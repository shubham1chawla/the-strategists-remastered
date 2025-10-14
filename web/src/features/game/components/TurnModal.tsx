import { useEffect, useState } from 'react';
import { Button, Modal, Space, Tag } from 'antd';
import { HomeOutlined } from '@ant-design/icons';
import useGameState from '@game/hooks/useGameState';
import useLoginState from '@login/hooks/useLoginState';

function TurnModal() {
  const { player } = useLoginState();
  const { lands, winnerPlayer } = useGameState();
  const [open, setOpen] = useState(false);

  useEffect(() => {
    setOpen((player?.turn && !winnerPlayer) || false);
  }, [player, winnerPlayer]);

  if (!player) {
    return null;
  }
  return (
    <Modal
      open={!!player && open}
      footer={
        <Button type="primary" onClick={() => setOpen(false)}>
          Got it!
        </Button>
      }
      onCancel={() => setOpen(false)}
      width={400}
      title="It's your turn!"
      centered
    >
      <Space>
        You have landed on
        <Tag icon={<HomeOutlined />}>{lands[player.index].name}</Tag>
      </Space>
    </Modal>
  );
}

export default TurnModal;
