import { useMemo } from 'react';
import { useSelector } from 'react-redux';
import { State } from '@/store';

const useGame = () => {
  const game = useSelector((state: State) => state.game);
  const { players, state } = game;

  // Determining turn player
  const turnPlayer = useMemo(
    () => (state === 'ACTIVE' ? players.find((p) => !!p.turn) : undefined),
    [state, players]
  );

  // Determining active players
  const activePlayers = useMemo(
    () => players.filter((p) => p.state === 'ACTIVE'),
    [players]
  );

  // Determining bankrupt players
  const bankruptPlayers = useMemo(
    () => players.filter((p) => p.state === 'BANKRUPT'),
    [players]
  );

  // Determining winner player
  const winnerPlayer = useMemo(
    () =>
      state === 'ACTIVE' && activePlayers.length === 1
        ? activePlayers[0]
        : undefined,
    [state, activePlayers]
  );

  /**
   * Sorting players for lobby -
   * 1. Active players by net-worth and remaining skips
   * 2. Bankrupt players by bankruptcy order
   */
  const sortedPlayers = useMemo(
    () => [
      ...activePlayers.sort((p1, p2) => {
        if (
          !!p1.remainingSkipsCount &&
          !!p2.remainingSkipsCount &&
          p1.netWorth === p2.netWorth
        ) {
          return p2.remainingSkipsCount - p1.remainingSkipsCount;
        }
        return p2.netWorth - p1.netWorth;
      }),
      ...bankruptPlayers.sort(
        (p1, p2) => p2.bankruptcyOrder - p1.bankruptcyOrder
      ),
    ],
    [activePlayers, bankruptPlayers]
  );

  return {
    ...game,
    sortedPlayers,
    activePlayers,
    bankruptPlayers,
    turnPlayer,
    winnerPlayer,
  };
};

export default useGame;
