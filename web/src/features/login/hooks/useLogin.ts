import { useMemo } from 'react';
import { useSelector } from 'react-redux';
import { State } from '@/store';

const useLogin = () => {
  const login = useSelector((state: State) => state.login);
  const { playerId } = login;
  const { players } = useSelector((state: State) => state.game);

  // Determining logged-in player
  const player = useMemo(
    () => players.find(({ id }) => id === playerId),
    [players, playerId],
  );

  return { ...login, player };
};

export default useLogin;
