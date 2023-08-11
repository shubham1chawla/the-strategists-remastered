import {
  AuditOutlined,
  DollarOutlined,
  StockOutlined,
  UserOutlined,
  WalletOutlined,
} from '@ant-design/icons';
import { Card, Col, Divider, Row, Statistic, Tag } from 'antd';
import { useSelector } from 'react-redux';
import { State } from '../redux';

export const Stats = () => {
  const { players } = useSelector((state: State) => state.lobby);
  const { username } = useSelector((state: State) => state.user);
  const player = players.find((p) => p.username === username);

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
