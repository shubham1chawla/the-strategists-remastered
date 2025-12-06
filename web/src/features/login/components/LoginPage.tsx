import { useCallback } from 'react';
import { Card, Flex, Row, Space } from 'antd';
import { GithubOutlined } from '@ant-design/icons';
import StrategistsLogo from '@shared/components/StrategistsLogo';
import LoginWorkflowProvider from '@login/providers/loginWorkflowProvider';
import ActionsWorklfow from './ActionsWorkflow';
import EnteringWorkflow from './EnteringWorkflow';
import JoinActionWorkflow from './JoinActionWorkflow';
import RecaptchaWorkflow from './RecaptchaWorkflow';
import ResumeWorkflow from './ResumeWorkflow';
import UnreachableWorkflow from './UnreachableWorkflow';
import VerifiedLoginWorkflow from './VerifiedLoginWorkflow';
import VerifyingWorkflow from './VerifyingWorkflow';

function LoginPage() {
  const onGithubClick = useCallback(
    () =>
      window.open(
        'https://github.com/shubham1chawla/the-strategists-remastered/',
        '_blank',
      ),
    [],
  );
  return (
    <LoginWorkflowProvider>
      <Flex
        className="strategists-login strategists-wallpaper"
        justify="center"
        align="center"
      >
        <Card
          className="strategists-login__card strategists-glossy"
          title={
            <Row justify="center">
              <StrategistsLogo />
            </Row>
          }
          actions={[
            <Space>
              <GithubOutlined onClick={onGithubClick} /> GitHub
            </Space>,
          ]}
        >
          <RecaptchaWorkflow />
          <VerifyingWorkflow />
          <VerifiedLoginWorkflow />
          <ResumeWorkflow />
          <ActionsWorklfow />
          <JoinActionWorkflow />
          <EnteringWorkflow />
          <UnreachableWorkflow />
        </Card>
      </Flex>
    </LoginWorkflowProvider>
  );
}

export default LoginPage;
