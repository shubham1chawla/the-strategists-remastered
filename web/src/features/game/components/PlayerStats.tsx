import { Card, Col, Divider, Row, Space, Statistic, Tag } from 'antd';
import {
  AuditOutlined,
  CrownOutlined,
  DollarOutlined,
  HeartFilled,
  HeartOutlined,
  StockOutlined,
  UserOutlined,
  WalletOutlined,
} from '@ant-design/icons';
import useGameState from '@game/hooks/useGameState';
import { Player } from '@game/state';

interface PlayerStatsProps {
  player: Player;
  showRemainingSkipsCount?: boolean;
  winner?: boolean;
}

function PlayerStats(props: PlayerStatsProps) {
  const { game } = useGameState();
  const { player, winner, showRemainingSkipsCount } = props;

  // Determining whether to show remaining skip counts
  const shouldShowRemainingSkipsCount =
    !!player.remainingSkipsCount &&
    !!game.allowedSkipsCount &&
    !!showRemainingSkipsCount &&
    player.state !== 'BANKRUPT';

  const remainingSkipsCount = player.remainingSkipsCount || 0;
  const allowedSkipsCount = game.allowedSkipsCount || 0;
  const skipsCount = allowedSkipsCount - remainingSkipsCount;

  return (
    <div className="strategists-stats">
      <Row>
        <Col span={24}>
          <Divider>
            {winner ? (
              <Space>
                <CrownOutlined />
                <span>{player.username} won this round!</span>
              </Space>
            ) : (
              <Space>
                <Tag icon={<UserOutlined />}>{player?.username}</Tag>
                {player.state === 'BANKRUPT' && (
                  <Tag icon={<AuditOutlined />}>Bankrupt</Tag>
                )}
                {shouldShowRemainingSkipsCount && (
                  <Tag>
                    {[...Array(remainingSkipsCount)].map((_, i) => (
                      // eslint-disable-next-line react/no-array-index-key
                      <HeartFilled key={i} />
                    ))}
                    {[...Array(skipsCount)].map((_, i) => (
                      // eslint-disable-next-line react/no-array-index-key
                      <HeartOutlined key={i} />
                    ))}
                  </Tag>
                )}
              </Space>
            )}
          </Divider>
        </Col>
      </Row>
      <Row>
        <Col span={12}>
          <Card variant="borderless">
            <Statistic
              title={
                <Space>
                  <StockOutlined />
                  Net Worth
                </Space>
              }
              value={player?.netWorth}
              precision={2}
              prefix={<DollarOutlined />}
            />
          </Card>
        </Col>
        <Col span={12}>
          <Card variant="borderless">
            <Statistic
              title={
                <Space>
                  <WalletOutlined />
                  Cash
                </Space>
              }
              value={player?.cash}
              precision={2}
              prefix={<DollarOutlined />}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
}

export default PlayerStats;
