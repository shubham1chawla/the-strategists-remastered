import React, { CSSProperties, useEffect } from 'react';
import { Button, Divider, Form, Input, InputNumber, List } from 'antd';
import { CloseCircleOutlined } from '@ant-design/icons';
import { useDispatch, useSelector } from 'react-redux';
import { addPlayer, removePlayer } from '../redux/admin/lobby/lobbyActions';
import { Player } from '../redux/admin/lobby/lobbyReducer';
import axios from 'axios';

const AdminLobby = () => {
  const dispatch = useDispatch();
  const players = useSelector((state: any) => state.lobby.players);
  const [form] = Form.useForm();

  useEffect(() => {
    axios.get('/api/players').then(async (res) => {
      await res.data.forEach((player: any) => {
        dispatch(addPlayer({ name: player.username, cash: player.cash }));
      });
    });
  }, []);

  const deleteFromList = async (item: Player) => {
    console.log(item);
    await axios
      .delete('/api/players', {
        data: {
          username: item.name,
        },
      })
      .then(() => {
        dispatch(removePlayer(item));
      });
  };

  const onFinish = async (values: any) => {
    console.log('Success:', values);
    const player: Player = {
      name: values.name,
      cash: values.cash,
    };
    await axios
      .post('/api/players', {
        username: player.name,
        cash: player.coins,
      })
      .then((response) => {
        dispatch(addPlayer(player));
        form.resetFields();
      });
  };

  const onFinishFailed = (errorInfo: any) => {
    console.log('Failed:', errorInfo);
  };

  return (
    <div>
      <h2>Add Player</h2>
      <div style={lobbyFormContainer}>
        <Form
          form={form}
          name="basic"
          onFinish={onFinish}
          onFinishFailed={onFinishFailed}
          autoComplete="off"
          style={{ width: '80%' }}
        >
          <div style={{ display: 'flex', flexDirection: 'row', width: '100%' }}>
            <div style={{ flex: '70%', marginRight: 8 }}>
              <Form.Item name="name">
                <Input size="middle" placeholder="Name" />
              </Form.Item>
            </div>
            <div style={{ flex: '30%' }}>
              <Form.Item name="cash">
                <InputNumber
                  size="middle"
                  placeholder="Cash"
                  min={1}
                  max={1000}
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
            Active Players
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
                    deleteFromList(item);
                  }}
                  style={{
                    fontSize: '20px',
                    color: '#e74c3c',
                    cursor: 'pointer',
                  }}
                />
              }
            >
              {item.name}
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

const list: CSSProperties = {
  color: '#fafafa',
  borderBlockEndColor: '#434343',
  fontSize: 16,
};

export default AdminLobby;
