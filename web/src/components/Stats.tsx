import {
  AuditOutlined,
  DollarOutlined,
  StockOutlined,
  UserOutlined,
  WalletOutlined,
} from '@ant-design/icons';
import { Card, Col, Divider, Row, Space, Statistic, Tag } from 'antd';
import { Player } from '../redux';

export interface StatsProps {
  player: Player;
}

export const Stats = (props: StatsProps) => {
  const { player } = props;
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
        <Col span={12}>
          <Card bordered={false}>
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
      </Row>
    </div>
  );
};
