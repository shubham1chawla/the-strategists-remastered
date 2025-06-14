import { Space } from 'antd';
import { LoadingOutlined } from '@ant-design/icons';
import useLoginWorkflow from '@login/hooks/useLoginWorkflow';

const EnteringWorkflow = () => {
  const { loginWorkflow } = useLoginWorkflow();
  if (loginWorkflow !== 'ENTERING') return null;
  return (
    <Space>
      <LoadingOutlined />
      Entering...
    </Space>
  );
};

export default EnteringWorkflow;
