import { Button, Divider, Row } from 'antd';
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
  return (
    <LoginWorkflowProvider>
      <main className="strategists-login strategists-wallpaper">
        <section className="strategists-login__workflows strategists-glossy">
          <Divider>
            <StrategistsLogo />
          </Divider>
          <br />
          <Row justify="center">
            <RecaptchaWorkflow />
            <VerifyingWorkflow />
            <VerifiedLoginWorkflow />
            <ResumeWorkflow />
            <ActionsWorklfow />
            <JoinActionWorkflow />
            <EnteringWorkflow />
            <UnreachableWorkflow />
          </Row>
          <br />
          <Divider>
            <Button
              target="_blank"
              href="https://github.com/shubham1chawla/the-strategists-remastered/issues"
              type="text"
              icon={<GithubOutlined />}
            >
              Contact
            </Button>
          </Divider>
        </section>
      </main>
    </LoginWorkflowProvider>
  );
}

export default LoginPage;
