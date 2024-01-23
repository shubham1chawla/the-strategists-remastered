import { MouseEvent } from 'react';
import {
  Button,
  Form,
  Input,
  InputNumber,
  List,
  Space,
  Tag,
  Tooltip,
  notification,
} from 'antd';
import {
  UserAddOutlined,
  UserDeleteOutlined,
  UserOutlined,
  WalletOutlined,
  StockOutlined,
  CrownOutlined,
  MailOutlined,
} from '@ant-design/icons';
import { useSelector } from 'react-redux';
import { Player, State } from '../redux';
import axios from 'axios';

/**
 * -----  UTILITIES DEFINED BELOW  -----
 */

const MIN_CASH_AMOUNT = 100;
const MAX_CASH_AMOUNT = 9999;

/**
 * -----  LOBBY COMPONENT BELOW  -----
 */

export const Lobby = () => {
  return (
    <>
      <LobbyPlayers />
      <InvitePlayerForm />
    </>
  );
};

/**
 * -----  LOBBY PLAYERS COMPONENT BELOW  -----
 */

const LobbyPlayers = () => {
  const { state, players } = useSelector((state: State) => state.lobby);

  // Sorting players in decreasing order of net-worth
  players.sort((p1, p2) => p2.netWorth - p1.netWorth);

  const kickPlayer = (event: MouseEvent, { id }: Player) => {
    event.stopPropagation();
    axios.delete('/api/players', { data: { playerId: id } });
  };

  return (
    <List
      className="strategists-lobby__players"
      size="large"
      dataSource={players}
      renderItem={(player: Player, index: number) => (
        <List.Item
          className={
            'strategists-lobby__players__player' +
            (player.state === 'BANKRUPT' ? ' strategists-striped' : '')
          }
          extra={
            <Tooltip
              title={
                state === 'ACTIVE'
                  ? `The Strategists in session, you can't kick ${player.username} now!`
                  : `Kick ${player.username} out!`
              }
            >
              <Button
                disabled={state === 'ACTIVE'}
                className="strategists-lobby__players__player__kick"
                type="text"
                shape="circle"
                onClick={(event) => kickPlayer(event, player)}
                icon={<UserDeleteOutlined />}
              />
            </Tooltip>
          }
        >
          <Space direction="vertical">
            <Space>
              <UserOutlined /> {player.username}
            </Space>
            <Space>
              {state === 'ACTIVE' && player.state !== 'BANKRUPT' ? (
                <Tooltip title={<>{player.username}'s rank</>}>
                  <Tag icon={<CrownOutlined />}>#{index + 1}</Tag>
                </Tooltip>
              ) : null}
              {player.state === 'INVITED' && (
                <Tag icon={<MailOutlined />}>Invited</Tag>
              )}
              <Tooltip title={<>{player.username}'s cash</>}>
                <Tag icon={<WalletOutlined />}>{player.cash}</Tag>
              </Tooltip>
              <Tooltip title={<>{player.username}'s net worth</>}>
                <Tag icon={<StockOutlined />}>{player.netWorth}</Tag>
              </Tooltip>
            </Space>
          </Space>
        </List.Item>
      )}
    />
  );
};

/**
 * -----  ADD PLAYER FORM COMPONENT BELOW  -----
 */

interface Invite {
  email: string;
  cash: number;
}

const InvitePlayerForm = () => {
  const { state } = useSelector((state: State) => state.lobby);
  const [api, contextHolder] = notification.useNotification();
  const [form] = Form.useForm();

  // Hiding invite player form when game starts
  if (state === 'ACTIVE') return null;

  const invitePlayer = async (invite: Invite) => {
    try {
      await axios.post('/api/players', invite);
    } catch (error) {
      api.error({ message: `Unable to invite ${invite.email}` });
    }
    form.resetFields();
  };

  return (
    <>
      {contextHolder}
      <Form
        layout="inline"
        form={form}
        name="basic"
        className="strategists-lobby__form"
        onFinish={invitePlayer}
        onFinishFailed={() =>
          api.error({ message: 'Incorrect player details!' })
        }
      >
        <Space.Compact size="large">
          <Form.Item
            noStyle
            name="email"
            rules={[{ required: true, type: 'email' }]}
          >
            <Input
              placeholder="Enter player's email"
              prefix={<UserOutlined />}
              type="email"
              required
            />
          </Form.Item>
          <Form.Item
            noStyle
            name="cash"
            rules={[{ required: true, type: 'number' }]}
          >
            <InputNumber
              placeholder="Cash"
              min={MIN_CASH_AMOUNT}
              max={MAX_CASH_AMOUNT}
              prefix={<WalletOutlined />}
              required
              type="number"
            />
          </Form.Item>
          <Form.Item noStyle>
            <Button
              type="primary"
              htmlType="submit"
              icon={<UserAddOutlined />}
            />
          </Form.Item>
        </Space.Compact>
      </Form>
    </>
  );
};
