import { MouseEvent } from 'react';
import { Alert, Button, List, Row, Space, Tag, Tooltip } from 'antd';
import {
  UserDeleteOutlined,
  UserOutlined,
  WalletOutlined,
  StockOutlined,
  CrownOutlined,
  HeartOutlined,
  UserAddOutlined,
  StarOutlined,
} from '@ant-design/icons';
import axios from 'axios';
import useGameState from '@game/hooks/useGameState';
import { Player } from '@game/state';
import useLoginState from '@login/hooks/useLoginState';

function Lobby() {
  const { gameCode, player } = useLoginState();
  const { game, sortedPlayers } = useGameState();

  const kickPlayer = (event: MouseEvent, { id }: Player) => {
    event.stopPropagation();
    axios.delete(`/api/games/${gameCode}/players`, { data: { playerId: id } });
  };

  return (
    <div className="strategists-lobby">
      <List
        className="strategists-lobby__players"
        size="large"
        dataSource={sortedPlayers}
        renderItem={(p: Player, index: number) => (
          <List.Item
            className={p.state === 'BANKRUPT' ? 'strategists-striped' : ''}
            extra={
              player?.host &&
              p.id !== player.id && (
                <Tooltip
                  title={
                    game.state === 'ACTIVE'
                      ? `The Strategists in session, you can't kick ${p.username} now!`
                      : `Kick ${p.username} out!`
                  }
                >
                  <Button
                    disabled={game.state === 'ACTIVE'}
                    type="text"
                    shape="circle"
                    onClick={(event) => kickPlayer(event, p)}
                    icon={<UserDeleteOutlined />}
                  />
                </Tooltip>
              )
            }
          >
            <Space direction="vertical">
              <Space>
                <UserOutlined /> {p.username}
              </Space>
              <Space>
                {game.state === 'ACTIVE' && p.state !== 'BANKRUPT' && (
                  <Tooltip title={<>{p.username}&apos;s rank</>}>
                    <Tag icon={<CrownOutlined />}>#{index + 1}</Tag>
                  </Tooltip>
                )}
                <Tooltip title={<>{p.username}&apos;s cash</>}>
                  <Tag icon={<WalletOutlined />}>{p.cash}</Tag>
                </Tooltip>
                <Tooltip title={<>{p.username}&apos;s net worth</>}>
                  <Tag icon={<StockOutlined />}>{p.netWorth}</Tag>
                </Tooltip>
                {!!p.remainingSkipsCount &&
                  player?.host &&
                  game.state === 'ACTIVE' && (
                    <Tooltip
                      title={`Remaining skips allowed before ${p.username} will be declared bankrupt.`}
                    >
                      <Tag icon={<HeartOutlined />}>
                        {p.remainingSkipsCount}
                      </Tag>
                    </Tooltip>
                  )}
                {p.host && <Tag icon={<StarOutlined />}>Host</Tag>}
              </Space>
            </Space>
          </List.Item>
        )}
      />
      {game.state === 'LOBBY' ? (
        <Row justify="center">
          <Alert
            type="info"
            message={
              <Space>
                Use code <Tag icon={<UserAddOutlined />}>{gameCode}</Tag> to
                join The Strategists.
              </Space>
            }
            showIcon
            banner
          />
        </Row>
      ) : null}
    </div>
  );
}

export default Lobby;
