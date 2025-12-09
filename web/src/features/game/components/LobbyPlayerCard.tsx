import { useCallback, MouseEvent } from 'react';
import { Button, Divider, Row, Space, Tag, Tooltip } from 'antd';
import {
  AuditOutlined,
  CrownOutlined,
  StarOutlined,
  UserDeleteOutlined,
} from '@ant-design/icons';
import axios from 'axios';
import useGameState from '@game/hooks/useGameState';
import { Player } from '@game/state';
import useLoginState from '@login/hooks/useLoginState';
import PlayerAvatar from './PlayerAvatar';
import PlayerCard from './PlayerCard';
import RemainingSkips from './RemainingSkips';

interface LobbyPlayerCardProps {
  player: Player;
  rank?: number;
  highlight?: boolean;
  showKickPlayer?: boolean;
}

function LobbyPlayerCard({
  player,
  rank,
  highlight,
  showKickPlayer,
}: LobbyPlayerCardProps) {
  const { gameCode, player: loggedInPlayer } = useLoginState();
  const { game } = useGameState();

  const kickPlayer = useCallback(
    (event: MouseEvent, { id }: Player) => {
      event.stopPropagation();
      axios.delete(`/api/games/${gameCode}/players`, {
        data: { playerId: id },
      });
    },
    [gameCode],
  );

  // Show Kick Player button to host player
  const showKickPlayerButton =
    showKickPlayer &&
    game.state !== 'ACTIVE' &&
    loggedInPlayer?.host &&
    player.id !== loggedInPlayer.id;

  return (
    <PlayerCard
      player={player}
      title={
        <Row align="middle">
          <Space align="center">
            <PlayerAvatar username={player.username} />
            {player.username}
          </Space>
          {game.allowedSkipsCount && (
            <>
              <Divider orientation="vertical" size="large" />
              <RemainingSkips player={player} />
            </>
          )}
        </Row>
      }
      extra={
        <>
          {showKickPlayerButton && (
            <Tooltip title={`Kick ${player.username} out!`}>
              <Button
                type="text"
                shape="circle"
                onClick={(event) => kickPlayer(event, player)}
                icon={<UserDeleteOutlined />}
              />
            </Tooltip>
          )}
          {game.state !== 'ACTIVE' && player.host && (
            <Tag icon={<StarOutlined />}>Host</Tag>
          )}
          {game.state === 'ACTIVE' && player.state !== 'BANKRUPT' && !!rank && (
            <Tooltip title={<>{player.username}&apos;s rank</>}>
              <Tag icon={<CrownOutlined />} variant="outlined">
                #{rank}
              </Tag>
            </Tooltip>
          )}
          {game.state === 'ACTIVE' && player.state === 'BANKRUPT' && (
            <Tag icon={<AuditOutlined />} variant="outlined">
              Bankrupt
            </Tag>
          )}
        </>
      }
      highlight={highlight}
    />
  );
}

export default LobbyPlayerCard;
