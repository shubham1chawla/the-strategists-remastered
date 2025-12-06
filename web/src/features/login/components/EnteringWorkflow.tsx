import { Row, Space } from 'antd';
import { LoadingOutlined } from '@ant-design/icons';
import useLoginWorkflow from '@login/hooks/useLoginWorkflow';

function EnteringWorkflow() {
  const { loginWorkflow } = useLoginWorkflow();
  if (loginWorkflow !== 'ENTERING') return null;
  return (
    <Row justify="center">
      <Space>
        <LoadingOutlined />
        Entering...
      </Space>
    </Row>
  );
}

export default EnteringWorkflow;
