import { Alert, Card, Row, Space, Tag } from 'antd';
import {
  CheckCircleOutlined,
  CheckOutlined,
  ExclamationCircleOutlined,
} from '@ant-design/icons';
import { Advice, useAdvices } from '../redux';

/**
 * -----  ADVICES COMPONENT BELOW  -----
 */

export const Advices = () => {
  const { playerAdvices } = useAdvices();
  return (
    <Space direction="vertical" className="strategists-advice">
      {!playerAdvices.length && (
        <div className="strategists-advice__empty">
          <Card
            title={
              <Space>
                <CheckCircleOutlined />
                <span>No Advices!</span>
              </Space>
            }
          >
            You are doing great! We don't have any advice for you right now.
          </Card>
        </div>
      )}
      {playerAdvices.map((advice) => (
        <Alert
          key={advice.id}
          type={advice.state === 'NEW' ? 'error' : 'success'}
          message={
            <Row justify="space-between">
              {getAdviceTitle(advice)}
              {advice.viewed && (
                <Tag icon={<CheckOutlined />} bordered={false}>
                  Read
                </Tag>
              )}
            </Row>
          }
          description={getAdviceDescription(advice)}
          icon={
            advice.state === 'NEW' ? (
              <ExclamationCircleOutlined />
            ) : (
              <CheckCircleOutlined />
            )
          }
          banner
        />
      ))}
    </Space>
  );
};

const getAdviceTitle = (advice: Advice) => {
  const splits = advice.type.split('_');
  return splits.map((s) => `${s[0]}${s.slice(1).toLowerCase()}`).join(' ');
};

const getAdviceDescription = (advice: Advice) => {
  const { type, val1, val2 } = advice;
  switch (type) {
    case 'AVOID_TIMEOUT':
      return 'The game completed your turn because of inactivity. Please use the "Skip" button!';
    case 'CONCENTRATE_INVESTMENTS':
      return `You have invested all over the map; try to have at least ${val1} investments close by!`;
    case 'FREQUENTLY_INVEST':
      return `You have yet to invest in the last ${val1} turns. Try investing more to get a competitive edge!`;
    case 'POTENTIAL_BANKRUPTCY':
      return `You will go bankrupt if you land on ${val2}! Keep more than $${val1} to avoid bankruptcy.`;
    case 'SIGNIFICANT_INVESTMENTS':
      return `Try investing more than ${val1}% to get steep rent from others.`;
    default:
      console.warn(`Unknown advice type: ${type}`);
      return 'Unknown Advice';
  }
};
