import { useEffect, useState } from 'react';
import { Button, Divider, Form, Input, notification } from 'antd';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { State, UserActions } from '../redux';
import { useForm } from 'antd/es/form/Form';
import {
  GithubOutlined,
  LockOutlined,
  LoginOutlined,
  UserOutlined,
} from '@ant-design/icons';
import { Logo } from '.';
import axios from 'axios';

export const Login = () => {
  const user = useSelector((state: State) => state.user);
  const [loading, setLoading] = useState(false);
  const [api, contextHolder] = notification.useNotification();
  const [form] = useForm();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  // redirecting to dashboard if user logged-in
  useEffect(() => {
    if (!user.username) {
      return;
    }
    navigate('/dashboard');
  }, [navigate, user.username]);

  const loginPlayer = ({ username, password }: any) => {
    setLoading(true);
    axios
      .post('/api/auth', { username, password })
      .then(({ data }) => {
        form.resetFields();
        dispatch(UserActions.setUser({ username, type: data }));
        navigate('/dashboard');
      })
      .catch(({ response }) => {
        const { message } = response?.data as { message: string };
        api.error({ message });
        setLoading(false);
      });
  };

  return (
    <>
      {contextHolder}
      <main className="strategists-login strategists-wallpaper">
        <section className="strategists-login__card strategists-glossy">
          <Divider>
            <Logo />
          </Divider>
          <br />
          <Form
            layout="inline"
            name="basic"
            className="strategists-login__card__form"
            form={form}
            onFinish={loginPlayer}
            onFinishFailed={(event) => console.error(event)}
            autoComplete="off"
          >
            <Form.Item
              name="username"
              rules={[{ required: true, message: 'Username required!' }]}
            >
              <Input
                size="large"
                prefix={<UserOutlined />}
                placeholder="Username"
              />
            </Form.Item>
            <Form.Item
              name="password"
              rules={[{ required: true, message: 'Password required!' }]}
            >
              <Input
                type="password"
                placeholder="Password"
                size="large"
                prefix={<LockOutlined />}
              />
            </Form.Item>
            <Form.Item>
              <Button
                type="primary"
                htmlType="submit"
                size="large"
                loading={loading}
                icon={<LoginOutlined />}
              />
            </Form.Item>
          </Form>
          <br />
          <Divider>
            <Button
              target="_blank"
              href="https://github.com/shubham1chawla/the-strategists-remastered"
              type="text"
              icon={<GithubOutlined />}
            >
              Contact
            </Button>
          </Divider>
        </section>
      </main>
    </>
  );
};
