import { useEffect, useMemo, useState } from 'react';
import { useSelector } from 'react-redux';
import { State } from '../store';
import { useLogin } from './useLogin';
import axios from 'axios';

export const useAdvices = () => {
  const advices = useSelector((state: State) => state.advices);
  const { playerId, gameCode } = useLogin();
  const [unreadCount, setUnreadCount] = useState(0);

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

  // Updating unread count
  useEffect(() => {
    const count = playerAdvices.filter((advice) => !advice.viewed).length;
    setUnreadCount(count);
  }, [playerAdvices]);

  return {
    advices,
    playerAdvices,
    unreadCount,
    markAdvicesRead: async (): Promise<void> => {
      if (unreadCount < 1 || !gameCode || !playerId) {
        return;
      }
      try {
        await axios.patch(`/api/games/${gameCode}/players/${playerId}/advices`);
      } catch (error) {
        console.error('Unable to mark advices viewed!', error);
      }
    },
  };
};
