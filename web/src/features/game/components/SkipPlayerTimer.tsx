import { useCallback, useEffect, useMemo, useState } from 'react';
import { Button, Card, Space, Statistic } from 'antd';
import { CloseOutlined, FieldTimeOutlined } from '@ant-design/icons';
import useGameState from '@game/hooks/useGameState';

function SkipPlayerTimer() {
  const { game, turnPlayer, winnerPlayer } = useGameState();
  const [hidden, setHidden] = useState(!turnPlayer || winnerPlayer);
  const [deadline, setDeadline] = useState(0);

  // Listing to turn player
  useEffect(() => {
    setDeadline(Date.now() + (game.skipPlayerTimeout || 0));
    setHidden(!turnPlayer || winnerPlayer);
  }, [game.skipPlayerTimeout, turnPlayer, winnerPlayer]);

  const onClose = useCallback(() => setHidden(true), []);

  const className = useMemo(() => {
    const classes = ['strategists-skip-player-timer-card'];
    if (hidden) {
      classes.push('strategists-skip-player-timer-card-hidden');
    }
    return classes.join(' ');
  }, [hidden]);

  // Checking if player skipping is enabled
  if (!game.skipPlayerTimeout || !turnPlayer) {
    return null;
  }

  return (
    <Card className={className}>
      <Statistic.Timer
        type="countdown"
        format="s"
        suffix="seconds"
        value={deadline}
        title={
          <Space>
            <FieldTimeOutlined />
            <span>Skipping {turnPlayer.username}&apos;s turn in</span>
          </Space>
        }
      />
      <Button
        className="strategists-skip-player-timer-card__close-button"
        type="text"
        shape="square"
        size="small"
        icon={<CloseOutlined />}
        onClick={onClose}
      />
    </Card>
  );
}

export default SkipPlayerTimer;
