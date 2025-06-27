import { Space } from 'antd';
import { LoadingOutlined } from '@ant-design/icons';
import useLoginWorkflow from '@login/hooks/useLoginWorkflow';

function ResumeWorkflow() {
  const { loginWorkflow } = useLoginWorkflow();
  if (loginWorkflow !== 'RESUME') return null;
  return (
    <Space>
      <LoadingOutlined />
      Checking if you are part of any game...
    </Space>
  );
}

export default ResumeWorkflow;
