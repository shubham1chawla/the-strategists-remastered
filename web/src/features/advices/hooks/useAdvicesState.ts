import { useEffect, useMemo, useState } from 'react';
import { useSelector } from 'react-redux';
import axios from 'axios';
import { StrategistsState } from '@/store';
import useLoginState from '@login/hooks/useLoginState';

const useAdvicesState = () => {
  const advices = useSelector((state: StrategistsState) => state.advicesState);
  const { playerId, gameCode } = useLoginState();
  const [unreadCount, setUnreadCount] = useState(0);

  // Filtering advices based on logged in player
  const playerAdvices = useMemo(() => {
    const filteredAdvices = advices.filter(
      (advice) => advice.playerId === playerId,
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
    markAdvicesRead: async (): Promise<void> =>
      unreadCount < 1 || !gameCode || !playerId
        ? Promise.resolve()
        : axios.patch(`/api/games/${gameCode}/players/${playerId}/advices`),
  };
};

export default useAdvicesState;
