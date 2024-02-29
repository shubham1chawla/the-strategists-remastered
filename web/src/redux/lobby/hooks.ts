import { useSelector } from 'react-redux';
import { State } from '../store';

export const useLobby = () => {
  const lobby = useSelector((state: State) => state.lobby);

  // Sorting players in decreasing order of net-worth and remaining skips
  // Making a copy of players before sorting to avoid direct state mutation.
  // Reference to the issue -
  // https://stackoverflow.com/questions/41051302/react-and-redux-uncaught-error-a-state-mutation-was-detected-between-dispatche
  const sortedPlayers = [...lobby.players].sort((p1, p2) => {
    if (
      Number.isInteger(p1.remainingSkipsCount) &&
      Number.isInteger(p2.remainingSkipsCount) &&
      p1.netWorth === p2.netWorth
    ) {
      return (p2.remainingSkipsCount || 0) - (p1.remainingSkipsCount || 0);
    }
    return p2.netWorth - p1.netWorth;
  });

  // Determining turn player
  const turnPlayer =
    lobby.state === 'ACTIVE' ? lobby.players.find((p) => !!p.turn) : undefined;

  // Determining active players
  const activePlayers = lobby.players.filter((p) => p.state === 'ACTIVE');

  // Determining winner player
  const winnerPlayer =
    lobby.state === 'ACTIVE' && activePlayers.length === 1
      ? activePlayers[0]
      : undefined;

  return {
    ...lobby,
    sortedPlayers,
    turnPlayer,
    winnerPlayer,
  };
};
