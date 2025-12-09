import { useMemo } from 'react';
import { Card, Col, Flex, Row, Space, Statistic } from 'antd';
import {
  StockOutlined,
  DollarOutlined,
  PieChartOutlined,
  PercentageOutlined,
  WalletOutlined,
  DollarCircleOutlined,
} from '@ant-design/icons';
import { Land } from '@game/state';
import LandAvatar from './LandAvatar';

interface LandCardProps {
  land: Land;
  propsedOwnership?: number;
  investmentCost?: number;
  highlight?: boolean;
}

function LandCard({
  land,
  propsedOwnership,
  investmentCost,
  highlight,
}: LandCardProps) {
  const className = useMemo(() => {
    const classes = ['strategists-land-card'];
    if (highlight) {
      classes.push('strategists-land-card-highlighed');
    }
    return classes.join(' ');
  }, [highlight]);

  // Flags to show extra stats when investing (can be 0)
  const showInvestmentStats =
    propsedOwnership !== undefined &&
    propsedOwnership !== null &&
    investmentCost !== undefined &&
    investmentCost !== null;

  return (
    <Card
      className={className}
      title={
        <Space align="center">
          <LandAvatar name={land.name} />
          {land.name}
        </Space>
      }
    >
      <Flex orientation="vertical" gap="large">
        <Row>
          <Col span={12}>
            <Statistic
              title={
                <Space>
                  <StockOutlined />
                  Market Value
                </Space>
              }
              value={land.marketValue}
              precision={2}
              prefix={<DollarOutlined />}
            />
          </Col>
          <Col span={12}>
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
          </Col>
        </Row>
        {showInvestmentStats && (
          <Row>
            <Col span={12}>
              <Statistic
                title={
                  <Space>
                    <PieChartOutlined />
                    Proposed Ownership
                  </Space>
                }
                value={propsedOwnership}
                precision={0}
                suffix={<PercentageOutlined />}
              />
            </Col>
            <Col span={12}>
              <Statistic
                title={
                  <Space>
                    <WalletOutlined />
                    Investment Cost
                  </Space>
                }
                value={investmentCost}
                precision={2}
                prefix={<DollarCircleOutlined />}
              />
            </Col>
          </Row>
        )}
      </Flex>
    </Card>
  );
}

export default LandCard;
