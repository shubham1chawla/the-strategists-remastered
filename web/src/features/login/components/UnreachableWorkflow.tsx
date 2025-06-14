import { Alert } from 'antd';
import useLoginWorkflow from '@login/hooks/useLoginWorkflow';

const UnreachableWorkflow = () => {
  const { loginWorkflow } = useLoginWorkflow();
  if (loginWorkflow !== 'UNREACHABLE') return null;
  return (
    <Alert
      type="error"
      message="Servers are unreachable!"
      description="The game is presently in a developmental stage, and we frequently deactivate the servers to reduce expenses. If you wish to engage with The Strategists, kindly reach out to the developers for access."
      showIcon
      banner
    />
  );
};

export default UnreachableWorkflow;
