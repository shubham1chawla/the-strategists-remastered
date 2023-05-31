import { MouseEvent, useState } from 'react';
import { Button, Form, Input, InputNumber, List, Space, Tooltip } from 'antd';
import {
  LockOutlined,
  UnlockOutlined,
  UserAddOutlined,
  UserDeleteOutlined,
  UserOutlined,
  WalletOutlined,
} from '@ant-design/icons';
import { useSelector } from 'react-redux';
import { Player, State } from '../redux';
import axios from 'axios';

const MIN_CASH_AMOUNT = 100;
const MAX_CASH_AMOUNT = 9999;

interface Password {
  value: string;
  show: boolean;
}

export const Lobby = () => (
  <>
    {LobbyPlayers()}
    {AddPlayerForm()}
  </>
);

const LobbyPlayers = () => {
  const { players } = useSelector((state: State) => state.lobby);
  const [passwords, setPasswords] = useState<Map<number, Password>>(new Map());

  const kickPlayer = (event: MouseEvent, { username }: Player) => {
    event.stopPropagation();
    axios.delete('/api/players', { data: { username } });
  };

  const showPassword = async ({ id }: Player) => {
    let password = passwords.get(id);
    if (!password) {
      const { data } = await axios.get(`/api/players/${id}/password`);
      password = { value: data, show: true };
    }
    setPasswords(new Map(passwords.set(id, { ...password, show: true })));
  };

  const hidePassword = ({ id }: Player) => {
    const password = passwords.get(id);
    if (!password) {
      return;
    }
    setPasswords(new Map(passwords.set(id, { ...password, show: false })));
  };

  const renderPassword = ({ id }: Player) => {
    const password = passwords.get(id);
    return password && password.show ? (
      <>
        <UnlockOutlined /> {password.value}
      </>
    ) : (
      <>
        <LockOutlined /> ****
      </>
    );
  };

  return (
    <List
      className="strategists-lobby__players"
      size="large"
      dataSource={players}
      renderItem={(player: Player) => (
        <List.Item
          className="strategists-lobby__players__player"
          extra={
            <Tooltip title={`Kick ${player.username} out!`}>
              <Button
                className="strategists-lobby__players__player__kick"
                type="text"
                shape="circle"
                onClick={(event) => kickPlayer(event, player)}
                icon={<UserDeleteOutlined />}
              />
            </Tooltip>
          }
        >
          <div
            onMouseLeave={() => hidePassword(player)}
            className="strategists-lobby__players__player__content"
          >
            <div className="strategists-lobby__players__player__content__info">
              <span>
                <UserOutlined /> {player.username}
              </span>
              <span>
                <WalletOutlined /> {player.netWorth}
              </span>
            </div>
            <div
              className="strategists-lobby__players__player__content__password"
              onMouseDown={() => showPassword(player)}
              onMouseUp={() => hidePassword(player)}
            >
              <Tooltip
                title={`Hold down to reveal ${player.username}'s password`}
              >
                {renderPassword(player)}
              </Tooltip>
            </div>
          </div>
        </List.Item>
      )}
    />
  );
};

const AddPlayerForm = () => {
  const { state } = useSelector((state: State) => state.lobby);
  const [form] = Form.useForm();

  const addPlayer = async ({ username, cash }: Player) => {
    await axios.post('/api/players', { username, cash });
    form.resetFields();
  };

  return (
    <Form
      layout="inline"
      form={form}
      name="basic"
      className="strategists-lobby__form"
      onFinish={addPlayer}
      onFinishFailed={(event) => console.error(event)}
      autoComplete="off"
    >
      <Tooltip
        title={
          state === 'active'
            ? `The Strategists in session, you can't add players now!`
            : null
        }
      >
        <Space.Compact size="large">
          <Form.Item
            noStyle
            name="username"
            rules={[{ required: true, message: `Username required!` }]}
          >
            <Input
              disabled={state === 'active'}
              placeholder="Username"
              prefix={<UserOutlined />}
            />
          </Form.Item>
          <Form.Item
            noStyle
            name="cash"
            rules={[{ required: true, message: `Cash required!` }]}
          >
            <InputNumber
              disabled={state === 'active'}
              placeholder="Cash"
              min={MIN_CASH_AMOUNT}
              max={MAX_CASH_AMOUNT}
              prefix={<WalletOutlined />}
            />
          </Form.Item>
          <Form.Item noStyle>
            <Button
              disabled={state === 'active'}
              type="primary"
              htmlType="submit"
              icon={<UserAddOutlined />}
            />
          </Form.Item>
        </Space.Compact>
      </Tooltip>
    </Form>
  );
};
