import { Row, Space } from 'antd';
import { ExclamationCircleOutlined } from '@ant-design/icons';

interface EmptyContainerProps {
  message?: string;
}

const EmptyContainer = (props: EmptyContainerProps) => {
  const { message } = props;
  return (
    <Row justify="center" align="middle">
      <Space>
        <ExclamationCircleOutlined />
        <span>{message || 'No information available!'}</span>
      </Space>
    </Row>
  );
};

export default EmptyContainer;
