import { ExclamationCircleOutlined } from '@ant-design/icons';
import { Row, Space } from 'antd';

export interface EmptyProps {
  message?: string;
}

export const Empty = (props: EmptyProps) => {
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
