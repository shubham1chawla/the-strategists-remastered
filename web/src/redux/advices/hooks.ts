import { useMemo } from 'react';
import { useSelector } from 'react-redux';
import { State } from '../store';

export const useAdvices = () => {
  const { advice: advices, login } = useSelector((state: State) => state);
  const { playerId } = login;

  // Filtering advices based on logged in player
  const playerAdvices = useMemo(() => {
    const filteredAdvices = advices.filter(
      (advice) => advice.playerId === playerId
    );
    filteredAdvices.sort((a1, a2) => {
      if (a1.state !== a2.state) {
        return a1.state === 'NEW' ? -1 : 1;
      }
      return a1.priority - a2.priority;
    });
    return filteredAdvices;
  }, [advices, playerId]);

  return {
    advices,
    playerAdvices,
  };
};
