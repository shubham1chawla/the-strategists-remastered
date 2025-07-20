import { useEffect, useState } from 'react';
import { Button, Modal, Space, Tag } from 'antd';
import { HomeOutlined } from '@ant-design/icons';
import useGame from '@game/hooks/useGame';
import useLogin from '@login/hooks/useLogin';

function TurnModal() {
  const { player } = useLogin();
  const { lands, winnerPlayer } = useGame();
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
