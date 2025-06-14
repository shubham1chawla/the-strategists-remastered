import { useContext } from 'react';
import LoginWorkflowProvider, {
  LoginWorkflowContext,
} from '@login/providers/loginWorkflowProvider';

const useLoginWorkflow = () => {
  const value = useContext(LoginWorkflowContext);
  if (!value) {
    throw new Error(
      `'${useLoginWorkflow.name}' called outside ${LoginWorkflowProvider.name}!`,
    );
  }
  return value;
};

export default useLoginWorkflow;
