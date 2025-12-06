import { Alert, Row } from 'antd';
import { ExclamationCircleOutlined } from '@ant-design/icons';

interface EmptyContainerProps {
  message?: string;
}

function EmptyContainer({ message }: EmptyContainerProps) {
  return (
    <Row justify="center" align="middle">
      <Alert
        type="info"
        title={message || 'No information available!'}
        icon={<ExclamationCircleOutlined />}
        banner
      />
    </Row>
  );
}

export default EmptyContainer;
