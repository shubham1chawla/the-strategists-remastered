import { Space, Tooltip } from 'antd';
import { HeartFilled, HeartOutlined } from '@ant-design/icons';
import useGameState from '@game/hooks/useGameState';
import { Player } from '@game/state';

interface RemainingSkipsProps {
  player: Player;
}

function RemainingSkips({ player }: RemainingSkipsProps) {
  const { game } = useGameState();

  const allowedSkipsCount = game.allowedSkipsCount || 0;
  if (!allowedSkipsCount) return null;

  const remainingSkipsCount = player.remainingSkipsCount || 0;
  const skipsCount = allowedSkipsCount - remainingSkipsCount;

  return (
    <Tooltip
      title={`Remaining skips allowed before ${player.username} will be declared bankrupt.`}
    >
      <Space>
        {[...Array(remainingSkipsCount)].map((_, i) => (
          // eslint-disable-next-line react/no-array-index-key
          <HeartFilled key={i} />
        ))}
        {[...Array(skipsCount)].map((_, i) => (
          // eslint-disable-next-line react/no-array-index-key
          <HeartOutlined key={i} />
        ))}
      </Space>
    </Tooltip>
  );
}

export default RemainingSkips;
