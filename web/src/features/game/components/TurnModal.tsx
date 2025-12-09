import { useEffect, useState } from 'react';
import { Button, Modal, Space, Typography } from 'antd';
import useGameState from '@game/hooks/useGameState';
import useLoginState from '@login/hooks/useLoginState';
import LandAvatar from './LandAvatar';

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
      className="strategists-modal"
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
        <LandAvatar name={lands[player.index].name} />
        <Typography.Text strong>{lands[player.index].name}</Typography.Text>
      </Space>
    </Modal>
  );
}

export default TurnModal;
