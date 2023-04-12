import { MouseEvent } from 'react';
import { Button, Form, FormInstance, Input, InputNumber, List } from 'antd';
import {
  CloseCircleOutlined,
  DollarOutlined,
  TeamOutlined,
  UserAddOutlined,
  UserOutlined,
} from '@ant-design/icons';
import { useSelector } from 'react-redux';
import { Player } from '../redux';
import axios from 'axios';

const MIN_CASH_AMOUNT = 100;
const MAX_CASH_AMOUNT = 9999;

export const Lobby = () => {
  const players = useSelector((state: any) => state.lobby.players);
  const [form] = Form.useForm();

  return (
    <>
      {
        // Rendering Players
        renderLobbyPlayers(players)
      }
      {
        // Rendering Add Player form
        renderAddPlayerForm(form)
      }
    </>
  );
};

const renderAddPlayerForm = (form: FormInstance<any>) => {
  const addPlayer = async ({ username, cash }: Player) => {
    await axios.post('/api/players', { username, cash }).then(() => {
      form.resetFields();
    });
  };

  const addPlayerFailed = (errorInfo: any) => {
    console.error('Failed:', errorInfo);
  };

  return (
    <Form
      className="strategists-lobby__form"
      form={form}
      name="basic"
      onFinish={addPlayer}
      onFinishFailed={addPlayerFailed}
      autoComplete="off"
    >
      <Form.Item name="username">
        <Input size="large" placeholder="Username" />
      </Form.Item>
      <Form.Item name="cash">
        <InputNumber
          placeholder="Cash"
          size="large"
          min={MIN_CASH_AMOUNT}
          max={MAX_CASH_AMOUNT}
        />
      </Form.Item>
      <Form.Item>
        <Button type="primary" htmlType="submit" size="large">
          <UserAddOutlined /> Add Players
        </Button>
      </Form.Item>
    </Form>
  );
};

const renderLobbyPlayers = (players: Player[]) => {
  const kickPlayer = async ({ username }: Player) => {
    await axios.delete('/api/players', {
      data: { username },
    });
  };

  const onKickClick = (e: MouseEvent, player: Player) => {
    e.stopPropagation();
    kickPlayer(player);
  };

  if (players.length === 0) {
    return '';
  }
  return (
    <div className="strategists-lobby__list">
      <header>
        <TeamOutlined /> Joined Players
      </header>
      <List
        className="strategists-list"
        size="large"
        dataSource={players}
        renderItem={(player: Player) => (
          <List.Item
            className="strategists-list__item"
            extra={
              <CloseCircleOutlined
                className="strategists-list__item__kick-button"
                onClick={(e) => onKickClick(e, player)}
              />
            }
          >
            <div>
              <div>
                <UserOutlined /> {player.username}
              </div>
              <div>
                <DollarOutlined /> {player.netWorth}
              </div>
            </div>
          </List.Item>
        )}
      />
    </div>
  );
};
