import { MouseEvent, useCallback, useMemo } from 'react';
import { Button, Card, Col, Row, Space, Statistic, Tag, Tooltip } from 'antd';
import {
  UserOutlined,
  WalletOutlined,
  StockOutlined,
  CrownOutlined,
  StarOutlined,
  UserDeleteOutlined,
  DollarOutlined,
  AuditOutlined,
} from '@ant-design/icons';
import axios from 'axios';
import useGameState from '@game/hooks/useGameState';
import { Player } from '@game/state';
import useLoginState from '@login/hooks/useLoginState';
import RemainingSkips from './RemainingSkips';

interface PlayerCardProps {
  player: Player;
  rank?: number;
  highlight?: boolean;
  showSkips?: boolean;
  showKickPlayer?: boolean;
}

function PlayerCard({
  player,
  rank,
  highlight,
  showSkips,
  showKickPlayer,
}: PlayerCardProps) {
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

  const className = useMemo(() => {
    const classes = ['strategists-player-card'];
    if (highlight) {
      classes.push('strategists-player-card-highlighed');
    }
    return classes.join(' ');
  }, [highlight]);

  // Show Kick Player button to host player
  const showKickPlayerButton =
    showKickPlayer &&
    game.state !== 'ACTIVE' &&
    loggedInPlayer?.host &&
    player.id !== loggedInPlayer.id;

  return (
    <Card
      className={className}
      title={
        <Space>
          <Tag icon={<UserOutlined />} variant="outlined">
            {player.username}
          </Tag>
          {showSkips && <RemainingSkips player={player} />}
        </Space>
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
    >
      <Row>
        <Col span={12}>
          <Statistic
            title={
              <Space>
                <StockOutlined />
                Net Worth
              </Space>
            }
            value={player?.netWorth}
            precision={2}
            prefix={<DollarOutlined />}
          />
        </Col>
        <Col span={12}>
          <Statistic
            title={
              <Space>
                <WalletOutlined />
                Cash
              </Space>
            }
            value={player?.cash}
            precision={2}
            prefix={<DollarOutlined />}
          />
        </Col>
      </Row>
    </Card>
  );
}

export default PlayerCard;
