import { Card, Col, Divider, Row, Space, Statistic, Tag } from 'antd';
import {
  DollarOutlined,
  HomeOutlined,
  PercentageOutlined,
  PieChartOutlined,
  StockOutlined,
} from '@ant-design/icons';
import { Land } from '@game/state';

interface LandStatsProps {
  land: Land;
}

const LandStats = (props: LandStatsProps) => {
  const { land } = props;
  return (
    <div className="strategists-stats">
      <Row>
        <Col span={24}>
          <Divider>
            <Tag icon={<HomeOutlined />}>{land?.name}</Tag>
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

export default LandStats;
