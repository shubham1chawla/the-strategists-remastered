import { ReactNode, useMemo } from 'react';
import { Card, Col, Row, Space, Statistic } from 'antd';
import {
  WalletOutlined,
  StockOutlined,
  DollarOutlined,
} from '@ant-design/icons';
import { Player } from '@game/state';
import PlayerAvatarTitle from './PlayerAvatarTitle';

interface PlayerCardProps {
  player: Player;
  title?: ReactNode;
  extra?: ReactNode;
  highlight?: boolean;
}

function PlayerCard({ player, title, extra, highlight }: PlayerCardProps) {
  const className = useMemo(() => {
    const classes = ['strategists-player-card'];
    if (highlight) {
      classes.push('strategists-player-card-highlighed');
    }
    return classes.join(' ');
  }, [highlight]);

  return (
    <Card
      className={className}
      title={title || <PlayerAvatarTitle player={player} />}
      extra={extra}
    >
      <Row>
        <Col span={12}>
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
        </Col>
        <Col span={12}>
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
        </Col>
      </Row>
    </Card>
  );
}

export default PlayerCard;
