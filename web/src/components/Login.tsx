import { Button } from 'antd';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { UserActions } from '../redux';

export const Login = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  function goToAdminDashboard() {
    dispatch(UserActions.setUser({ type: 'admin', username: 'Admin' }));
    navigate('/dashboard');
  }

  function goToPlayerDashboard() {
    dispatch(UserActions.setUser({ type: 'player', username: 'Unknown' }));
    navigate('/dashboard');
  }

  return (
    <div>
      <Button onClick={goToPlayerDashboard}>Player</Button>
      <Button onClick={goToAdminDashboard}>Admin</Button>
    </div>
  );
};
