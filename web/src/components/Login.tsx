import { Button } from 'antd';
import { useDispatch } from 'react-redux';
import { setUser } from '../redux/user';
import { useNavigate } from 'react-router-dom';

export const Login = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  function goToAdminDashboard() {
    dispatch(setUser({ type: 'admin', username: 'Admin' }));
    navigate('/dashboard');
  }

  function goToPlayerDashboard() {
    dispatch(setUser({ type: 'player', username: 'Unknown' }));
    navigate('/dashboard');
  }

  return (
    <div>
      <Button onClick={goToPlayerDashboard}>Player</Button>
      <Button onClick={goToAdminDashboard}>Admin</Button>
    </div>
  );
};
