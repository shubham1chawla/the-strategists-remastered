import { MouseEvent, useState } from 'react';
import { Button, Form, Input, InputNumber, List, Tooltip } from 'antd';
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
  const [form] = Form.useForm();

  const addPlayer = async ({ username, cash }: Player) => {
    await axios.post('/api/players', { username, cash });
    form.resetFields();
  };

  return (
    <Form
      className="strategists-lobby__form"
      form={form}
      name="basic"
      onFinish={addPlayer}
      onFinishFailed={(event) => console.error(event)}
      autoComplete="off"
    >
      <Form.Item name="username" className="strategists-lobby__form__username">
        <Input size="large" placeholder="Username" />
      </Form.Item>
      <Form.Item name="cash" className="strategists-lobby__form__cash">
        <InputNumber
          placeholder="Cash"
          size="large"
          min={MIN_CASH_AMOUNT}
          max={MAX_CASH_AMOUNT}
        />
      </Form.Item>
      <Form.Item className="strategists-lobby__form__add">
        <Button type="primary" htmlType="submit" size="large">
          <UserAddOutlined /> Add Players
        </Button>
      </Form.Item>
    </Form>
  );
};
