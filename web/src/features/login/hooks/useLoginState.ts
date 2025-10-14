import { useMemo } from 'react';
import { useSelector } from 'react-redux';
import { StrategistsState } from '@/store';

const useLoginState = () => {
  const login = useSelector((state: StrategistsState) => state.loginState);
  const { playerId } = login;
  const { players } = useSelector((state: StrategistsState) => state.gameState);

  // Determining logged-in player
  const player = useMemo(
    () => players.find(({ id }) => id === playerId),
    [players, playerId],
  );

  return { ...login, player };
};

export default useLoginState;
