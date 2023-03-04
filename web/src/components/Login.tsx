import React from 'react';
import { Button } from 'antd';
import { useDispatch } from 'react-redux';
import { setUser } from '../redux/game/gameActions';
import { useNavigate } from 'react-router-dom';

const Login = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  function goToAdminDashboard() {
    dispatch(setUser('admin'));
    navigate('/dashboard');
  }

  function goToPlayerDashboard() {
    dispatch(setUser('player'));
    navigate('/dashboard');
  }

  return (
    <div>
      <Button onClick={goToPlayerDashboard}>Player</Button>
      <Button onClick={goToAdminDashboard}>Admin</Button>
    </div>
  );
};

export default Login;
