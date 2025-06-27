import { Alert, Card, Row, Space, Tag } from 'antd';
import {
  CheckCircleOutlined,
  CheckOutlined,
  ExclamationCircleOutlined,
} from '@ant-design/icons';
import useAdvices from '@advices/hooks/useAdvices';
import AdviceDescription from './AdviceDescription';
import AdviceTitle from './AdviceTitle';

function Advices() {
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
            You are doing great! We don&apos;t have any advice for you right
            now.
          </Card>
        </div>
      )}
      {playerAdvices.map((advice) => (
        <Alert
          key={advice.id}
          type={advice.state === 'NEW' ? 'error' : 'success'}
          message={
            <Row justify="space-between">
              <AdviceTitle advice={advice} />
              {advice.viewed && (
                <Tag icon={<CheckOutlined />} bordered={false}>
                  Read
                </Tag>
              )}
            </Row>
          }
          description={<AdviceDescription advice={advice} />}
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
}

export default Advices;
