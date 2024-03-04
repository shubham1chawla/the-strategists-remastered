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
import { Player, useLobby, useLogin } from '../redux';
import axios from 'axios';

/**
 * -----  LOBBY COMPONENT BELOW  -----
 */

export const Lobby = () => {
  return (
    <div className="strategists-lobby">
      <LobbyPlayers />
      <GameCode />
    </div>
  );
};

/**
 * -----  LOBBY PLAYERS COMPONENT BELOW  -----
 */

const LobbyPlayers = () => {
  const { gameCode, player } = useLogin();
  const { state, sortedPlayers } = useLobby();

  const kickPlayer = (event: MouseEvent, { id }: Player) => {
    event.stopPropagation();
    axios.delete(`/api/games/${gameCode}/players`, { data: { playerId: id } });
  };

  return (
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
                  state === 'ACTIVE'
                    ? `The Strategists in session, you can't kick ${p.username} now!`
                    : `Kick ${p.username} out!`
                }
              >
                <Button
                  disabled={state === 'ACTIVE'}
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
              {state === 'ACTIVE' && p.state !== 'BANKRUPT' && (
                <Tooltip title={<>{p.username}'s rank</>}>
                  <Tag icon={<CrownOutlined />}>#{index + 1}</Tag>
                </Tooltip>
              )}
              <Tooltip title={<>{p.username}'s cash</>}>
                <Tag icon={<WalletOutlined />}>{p.cash}</Tag>
              </Tooltip>
              <Tooltip title={<>{p.username}'s net worth</>}>
                <Tag icon={<StockOutlined />}>{p.netWorth}</Tag>
              </Tooltip>
              {!!p.remainingSkipsCount &&
                player?.host &&
                state === 'ACTIVE' && (
                  <Tooltip
                    title={`Remaining skips allowed before ${p.username} will be declared bankrupt.`}
                  >
                    <Tag icon={<HeartOutlined />}>{p.remainingSkipsCount}</Tag>
                  </Tooltip>
                )}
              {p.host && <Tag icon={<StarOutlined />}>Host</Tag>}
            </Space>
          </Space>
        </List.Item>
      )}
    />
  );
};

/**
 * -----  GAME CODE COMPONENT BELOW  -----
 */

export const GameCode = () => {
  const { gameCode } = useLogin();
  const { state } = useLobby();

  if (state === 'ACTIVE') return null;

  return (
    <Row justify="center">
      <Alert
        type="info"
        message={
          <Space>
            Use code <Tag icon={<UserAddOutlined />}>{gameCode}</Tag> to join
            The Strategists.
          </Space>
        }
        showIcon
        banner
      />
    </Row>
  );
};
