import {
  AuditOutlined,
  DollarOutlined,
  StockOutlined,
  UserOutlined,
  WalletOutlined,
} from '@ant-design/icons';
import { Card, Col, Divider, Row, Statistic, Tag } from 'antd';
import { Player } from '../redux';

export interface StatsProps {
  player: Player;
}

export const Stats = (props: Partial<StatsProps>) => {
  const { player } = props;
  if (!player) {
    return null;
  }

  return (
    <div className="strategists-stats">
      {player?.state === 'BANKRUPT' ? (
        <div className="strategists-stats__bankrupt strategists-striped">
          <Tag icon={<AuditOutlined />}>Bankrupt</Tag>
        </div>
      ) : null}
      <Row>
        <Col span={24} className="strategists-stats__username">
          <Divider>
            <UserOutlined /> {player?.username}
          </Divider>
        </Col>
      </Row>
      <Row>
        <Col span={12}>
          <Card bordered={false}>
            <Statistic
              title={
                <>
                  <WalletOutlined /> Cash
                </>
              }
              value={player?.cash}
              precision={2}
              prefix={<DollarOutlined />}
            />
          </Card>
        </Col>
        <Col span={12}>
          <Card bordered={false}>
            <Statistic
              title={
                <>
                  <StockOutlined /> Net Worth
                </>
              }
              value={player?.netWorth}
              precision={2}
              prefix={<DollarOutlined />}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};
