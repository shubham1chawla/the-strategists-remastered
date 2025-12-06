import { Flex } from 'antd';
import useGameState from '@game/hooks/useGameState';
import { Player } from '@game/state';
import useLoginState from '@login/hooks/useLoginState';
import PlayerCard from './PlayerCard';

function Lobby() {
  const { player: loggedInPlayer } = useLoginState();
  const { sortedPlayers } = useGameState();
  return (
    <Flex className="strategists-lobby" orientation="vertical" gap="large">
      {sortedPlayers.map((p: Player, i: number) => (
        <PlayerCard
          key={p.id}
          player={p}
          rank={i + 1}
          highlight={loggedInPlayer?.id === p.id}
          showKickPlayer
          showSkips
        />
      ))}
    </Flex>
  );
}

export default Lobby;
