import { Row } from 'antd';
import { NotificationOutlined } from '@ant-design/icons';
import useGameState from '@game/hooks/useGameState';
import useLoginState from '@login/hooks/useLoginState';
import PlayerCard from './PlayerCard';

function WinnerPlayerCard() {
  const { player } = useLoginState();
  const { winnerPlayer } = useGameState();
  if (!winnerPlayer || !player) return null;
  return (
    <PlayerCard
      player={winnerPlayer}
      title={
        <Row justify="center">
          {player.id !== winnerPlayer.id ? (
            <span>
              <NotificationOutlined /> Game Over! {winnerPlayer.username} won
              this round.
            </span>
          ) : (
            <span>
              <NotificationOutlined /> Congratulations! You won this round.
            </span>
          )}
        </Row>
      }
    />
  );
}

export default WinnerPlayerCard;
