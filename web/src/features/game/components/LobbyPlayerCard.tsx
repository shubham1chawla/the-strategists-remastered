import { useCallback, MouseEvent, ReactNode } from 'react';
import { Button, Divider, Row, Tag, Tooltip } from 'antd';
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
import PlayerAvatarTitle from './PlayerAvatarTitle';
import PlayerCard from './PlayerCard';
import RemainingSkips from './RemainingSkips';

interface DefaultLobbyPlayerCardExtraProps {
  player: Player;
  rank?: number;
  showKickPlayer?: boolean;
}

function DefaultLobbyPlayerCardExtra({
  player,
  rank,
  showKickPlayer,
}: DefaultLobbyPlayerCardExtraProps) {
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
          <Tag icon={<CrownOutlined />}>#{rank}</Tag>
        </Tooltip>
      )}
      {game.state === 'ACTIVE' && player.state === 'BANKRUPT' && (
        <Tag icon={<AuditOutlined />}>Bankrupt</Tag>
      )}
    </>
  );
}

interface LobbyPlayerCardProps {
  player: Player;
  rank?: number;
  highlight?: boolean;
  showKickPlayer?: boolean;
  extra?: ReactNode;
}

function LobbyPlayerCard({
  player,
  rank,
  highlight,
  showKickPlayer,
  extra,
}: LobbyPlayerCardProps) {
  const { game } = useGameState();
  return (
    <PlayerCard
      player={player}
      title={
        <Row align="middle">
          <PlayerAvatarTitle
            player={player}
            clickable={player.state !== 'BANKRUPT'}
          />
          {game.allowedSkipsCount && player.state !== 'BANKRUPT' && (
            <>
              <Divider orientation="vertical" size="large" />
              <RemainingSkips player={player} />
            </>
          )}
        </Row>
      }
      extra={
        extra || (
          <DefaultLobbyPlayerCardExtra
            player={player}
            rank={rank}
            showKickPlayer={showKickPlayer}
          />
        )
      }
      highlight={highlight}
    />
  );
}

export default LobbyPlayerCard;
