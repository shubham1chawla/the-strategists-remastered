import {
  AuditOutlined,
  DollarOutlined,
  HomeOutlined,
  PercentageOutlined,
  PieChartOutlined,
  StockOutlined,
  UserOutlined,
  WalletOutlined,
} from '@ant-design/icons';
import { Card, Col, Divider, Row, Space, Statistic, Tag } from 'antd';
import { Land, Player } from '../redux';

/**
 * -----  PLAYER STATS BELOW  -----
 */

export interface PlayerStatsProps {
  player: Player;
}

export const PlayerStats = (props: PlayerStatsProps) => {
  const { player } = props;
  return (
    <div className="strategists-stats">
      {player?.state === 'BANKRUPT' ? (
        <div className="strategists-stats__bankrupt strategists-striped">
          <Tag icon={<AuditOutlined />}>Bankrupt</Tag>
        </div>
      ) : null}
      <Row>
        <Col span={24}>
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

/**
 * -----  LAND STATS BELOW  -----
 */

export interface LandStatsProps {
  land: Land;
}

export const LandStats = (props: LandStatsProps) => {
  const { land } = props;
  return (
    <div className="strategists-stats">
      <Row>
        <Col span={24}>
          <Divider>
            <HomeOutlined /> {land?.name}
          </Divider>
        </Col>
      </Row>
      <Row>
        <Col span={12}>
          <Card bordered={false}>
            <Statistic
              title={
                <Space>
                  <StockOutlined />
                  Market Value
                </Space>
              }
              value={land?.marketValue}
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
                  <PieChartOutlined />
                  Total Ownership
                </Space>
              }
              value={land?.totalOwnership}
              suffix={<PercentageOutlined />}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};
