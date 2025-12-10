import { useMemo } from 'react';
import { Space, Tooltip } from 'antd';
import usePortfolioModal from '@game/hooks/usePortfolioModal';
import { Player } from '@game/state';
import PlayerAvatar from './PlayerAvatar';

interface PlayerAvatarTitleProps {
  player: Player;
  clickable?: boolean;
}

function PlayerAvatarTitle({ player, clickable }: PlayerAvatarTitleProps) {
  const { setPortfolioModalProps } = usePortfolioModal();

  const onClick = clickable
    ? () =>
        setPortfolioModalProps({
          perspective: 'player',
          node: player,
        })
    : undefined;

  const className = useMemo(() => {
    const classes = ['strategists-player-avatar-title'];
    if (clickable) {
      classes.push('strategists-player-avatar-title-clickable');
    }
    return classes.join(' ');
  }, [clickable]);

  return (
    <Tooltip
      title={clickable ? `Open ${player.username}'s Portfolio` : undefined}
    >
      <Space
        className={className}
        align="center"
        role={clickable ? 'button' : undefined}
        onClick={onClick}
      >
        <PlayerAvatar username={player.username} />
        {player.username}
      </Space>
    </Tooltip>
  );
}

export default PlayerAvatarTitle;
