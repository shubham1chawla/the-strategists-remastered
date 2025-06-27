import { Space } from 'antd';
import { LoadingOutlined } from '@ant-design/icons';
import useLoginWorkflow from '@login/hooks/useLoginWorkflow';

function VerifyingWorkflow() {
  const { loginWorkflow } = useLoginWorkflow();
  if (loginWorkflow !== 'VERIFYING') return null;
  return (
    <Space>
      <LoadingOutlined />
      Verifying...
    </Space>
  );
}

export default VerifyingWorkflow;
