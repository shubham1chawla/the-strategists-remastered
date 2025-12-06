import { useMemo } from 'react';
import { Card, Row, Space, Tag } from 'antd';
import {
  CheckCircleOutlined,
  CheckOutlined,
  ExclamationCircleOutlined,
} from '@ant-design/icons';
import { Advice } from '@advices/state';
import AdviceTitle from './AdviceTitle';

interface AdviceCardProps {
  advice: Advice;
}

function AdviceCard({ advice }: AdviceCardProps) {
  const className = useMemo(() => {
    const classes = ['strategists-advice-card'];
    if (advice.state === 'NEW') {
      classes.push('strategists-advice-card-highlighed');
    }
    return classes.join(' ');
  }, [advice.state]);

  return (
    <Card className={className}>
      <Card.Meta
        title={
          <Row justify="space-between">
            <Space>
              {advice.state === 'NEW' ? (
                <ExclamationCircleOutlined />
              ) : (
                <CheckCircleOutlined />
              )}
              <AdviceTitle advice={advice} />
            </Space>
            {advice.viewed && (
              <Tag icon={<CheckOutlined />} variant="filled">
                Read
              </Tag>
            )}
          </Row>
        }
        description={advice.text}
      />
    </Card>
  );
}

export default AdviceCard;
