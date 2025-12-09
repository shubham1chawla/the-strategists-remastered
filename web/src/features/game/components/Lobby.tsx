import { Flex } from 'antd';
import useGameState from '@game/hooks/useGameState';
import { Player } from '@game/state';
import useLoginState from '@login/hooks/useLoginState';
import LobbyPlayerCard from './LobbyPlayerCard';

function Lobby() {
  const { player: loggedInPlayer } = useLoginState();
  const { sortedPlayers } = useGameState();
  return (
    <Flex className="strategists-lobby" orientation="vertical" gap="large">
      {sortedPlayers.map((p: Player, i: number) => (
        <LobbyPlayerCard
          key={p.id}
          player={p}
          rank={i + 1}
          highlight={loggedInPlayer?.id === p.id}
          showKickPlayer
        />
      ))}
    </Flex>
  );
}

export default Lobby;
