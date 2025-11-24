import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { Space } from 'antd';
import { LoadingOutlined } from '@ant-design/icons';
import axios from 'axios';
import useLoginWorkflow from '@login/hooks/useLoginWorkflow';
import { loggedIn, LoginState } from '@login/state';

function ResumeWorkflow() {
  const { loginWorkflow, setLoginWorkflow, googleLoginCredential } =
    useLoginWorkflow();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  // Checking if player already part of a game
  useEffect(() => {
    if (loginWorkflow !== 'RESUME' || !googleLoginCredential) return;
    axios
      .get<LoginState>(`/api/games?credential=${googleLoginCredential}`)
      .then(({ data }) => {
        dispatch(loggedIn(data));
        navigate('/game');
      })
      .catch(() => setLoginWorkflow('ACTIONS'));
  }, [
    loginWorkflow,
    setLoginWorkflow,
    googleLoginCredential,
    dispatch,
    navigate,
  ]);

  if (loginWorkflow !== 'RESUME') return null;
  return (
    <Space>
      <LoadingOutlined />
      Checking if you are part of any game...
    </Space>
  );
}

export default ResumeWorkflow;
