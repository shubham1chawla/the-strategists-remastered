import { CSSProperties } from 'react';
import { Button, Divider, Form, Input, InputNumber, List } from 'antd';
import { CloseCircleOutlined } from '@ant-design/icons';
import { useSelector } from 'react-redux';
import { Player } from '../redux';
import axios from 'axios';
import { list } from '../StylingConstants';

const MIN_CASH_AMOUNT = 100;
const MAX_CASH_AMOUNT = 9999;

export const Lobby = () => {
  const players = useSelector((state: any) => state.lobby.players);
  const [form] = Form.useForm();

  const kickPlayer = async ({ username }: Player) => {
    await axios.delete('/api/players', {
      data: { username },
    });
  };

  const addPlayer = async ({ username, cash }: Player) => {
    await axios.post('/api/players', { username, cash }).then(() => {
      form.resetFields();
    });
  };

  const addPlayerFailed = (errorInfo: any) => {
    console.error('Failed:', errorInfo);
  };

  return (
    <div>
      <h2>Add Player</h2>
      <div style={lobbyFormContainer}>
        <Form
          form={form}
          name="basic"
          onFinish={addPlayer}
          onFinishFailed={addPlayerFailed}
          autoComplete="off"
          style={{ width: '80%' }}
        >
          <div style={{ display: 'flex', flexDirection: 'row', width: '100%' }}>
            <div style={{ flex: '70%', marginRight: 8 }}>
              <Form.Item name="username">
                <Input size="middle" placeholder="Username" />
              </Form.Item>
            </div>
            <div style={{ flex: '30%' }}>
              <Form.Item name="cash">
                <InputNumber
                  size="middle"
                  placeholder="Cash"
                  min={MIN_CASH_AMOUNT}
                  max={MAX_CASH_AMOUNT}
                  style={{ width: '100%' }}
                />
              </Form.Item>
            </div>
          </div>

          <Form.Item>
            <Button
              style={{ width: '100%' }}
              size="middle"
              type="primary"
              htmlType="submit"
            >
              Add
            </Button>
          </Form.Item>
        </Form>
      </div>
      <div style={{ padding: 20 }}>
        {players.length > 0 ? (
          <Divider
            style={{ color: '#fafafa', borderBlockStart: 'rgb(67, 67, 67)' }}
            orientation="left"
          >
            Joined Players
          </Divider>
        ) : (
          ''
        )}
        <List
          size="large"
          dataSource={players}
          renderItem={(item: Player) => (
            <List.Item
              style={list}
              extra={
                <CloseCircleOutlined
                  onClick={(e) => {
                    e.stopPropagation();
                    kickPlayer(item);
                  }}
                  style={{
                    fontSize: '20px',
                    color: '#e74c3c',
                    cursor: 'pointer',
                  }}
                />
              }
            >
              <div>
                <h3>{item.username}</h3>
                <span>{item.cash}</span>
              </div>
            </List.Item>
          )}
        />
      </div>
    </div>
  );
};

const lobbyFormContainer: CSSProperties = {
  display: 'flex',
  justifyContent: 'center',
  width: '100%',
};
