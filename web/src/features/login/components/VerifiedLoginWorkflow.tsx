import { useCallback, useEffect, useState } from 'react';
import { Alert, Button, Col, Flex, Form, Input, Row } from 'antd';
import type { FormProps } from 'antd';
import { GoogleCredentialResponse, GoogleLogin } from '@react-oauth/google';
import useNotifications from '@shared/hooks/useNotifications';
import useLoginWorkflow from '@login/hooks/useLoginWorkflow';

function GoogleOAuthLoginWorkflow() {
  const { setLoginWorkflow, setGoogleLoginCredential } = useLoginWorkflow();
  const { errorNotification } = useNotifications();

  const handleGoogleLoginError = useCallback(() => {
    errorNotification({
      title:
        'Google authentication failed. Please contact the developers to address your issue!',
    });
    setLoginWorkflow('VERIFIED');
  }, [setLoginWorkflow, errorNotification]);

  const handleGoogleLoginSuccess = useCallback(
    ({ credential }: GoogleCredentialResponse) => {
      if (!credential) {
        handleGoogleLoginError();
        return;
      }
      setGoogleLoginCredential(credential);
      setLoginWorkflow('RESUME');
    },
    [handleGoogleLoginError, setLoginWorkflow, setGoogleLoginCredential],
  );

  return (
    <Row justify="center">
      <GoogleLogin
        onSuccess={handleGoogleLoginSuccess}
        onError={handleGoogleLoginError}
        theme="filled_black"
        size="medium"
        shape="rectangular"
        text="signin_with"
        useOneTap
      />
    </Row>
  );
}

type ManualLoginFormType = {
  name: string;
  email: string;
};

function ManualLoginWorkflow() {
  const { setLoginWorkflow, setGoogleLoginCredential } = useLoginWorkflow();
  const [form] = Form.useForm<ManualLoginFormType>();
  const [loginButtonDisabled, setLoginButtonDisabled] = useState(true);

  // Checking if login form should be enabled
  const loginFormValues = Form.useWatch([], form);
  useEffect(() => {
    const name: string | undefined = form.getFieldValue('name');
    const email: string | undefined = form.getFieldValue('email');
    if (name || email) {
      form
        .validateFields({ validateOnly: true })
        .then(() => setLoginButtonDisabled(false))
        .catch(() => setLoginButtonDisabled(true));
    } else {
      setLoginButtonDisabled(true);
    }
  }, [form, loginFormValues]);

  const handleManualLoginAction: FormProps<ManualLoginFormType>['onFinish'] =
    useCallback(
      (values: ManualLoginFormType) => {
        // Convert object to JSON string
        const jsonString = JSON.stringify(values);

        // Handle Unicode characters (if present)
        const utf8EncodedString = unescape(encodeURIComponent(jsonString));

        // Base64 encode the string
        const base64EncodedObject = btoa(utf8EncodedString);

        // Mocking Google OAuth Credential (Base64 Enncoded JWT details + '.' + user details)
        const credential = `JWT.${base64EncodedObject}`;

        setGoogleLoginCredential(credential);
        setLoginWorkflow('RESUME');
      },
      [setGoogleLoginCredential, setLoginWorkflow],
    );

  return (
    <Row justify="center">
      <Col span={15}>
        <Flex orientation="vertical" gap="large">
          <Form
            form={form}
            name="manual-login"
            layout="vertical"
            autoComplete="off"
            size="large"
            requiredMark={false}
            onFinish={handleManualLoginAction}
          >
            <Form.Item<ManualLoginFormType>
              label="Name"
              name="name"
              rules={[
                {
                  required: true,
                  type: 'string',
                  whitespace: true,
                  message: 'Please enter your name!',
                },
              ]}
            >
              <Input placeholder="Enter your name" />
            </Form.Item>
            <Form.Item<ManualLoginFormType>
              label="Email"
              name="email"
              rules={[
                {
                  required: true,
                  type: 'email',
                  message: 'Please enter a valid email address!',
                },
              ]}
            >
              <Input placeholder="Enter your email" />
            </Form.Item>
            <Form.Item noStyle>
              <Button
                htmlType="submit"
                type="primary"
                disabled={loginButtonDisabled}
                block
              >
                Login
              </Button>
            </Form.Item>
          </Form>
          <Alert
            type="info"
            title="Developer Note"
            description={
              <span>
                This manual login method is only for testing purposes. In a
                production environment, Google OAuth login should be used by
                setting <code>VITE_GOOGLE_OAUTH_CLIENT_ID</code> environment
                variable.
              </span>
            }
            showIcon
          />
        </Flex>
      </Col>
    </Row>
  );
}

function VerifiedLoginWorkflow() {
  const { loginWorkflow, googleOAuthClientId } = useLoginWorkflow();

  // Workflow must be VERIFIED to proceed
  if (loginWorkflow !== 'VERIFIED') return null;

  // Choose login workflow based on env variable
  return !googleOAuthClientId ? (
    <ManualLoginWorkflow />
  ) : (
    <GoogleOAuthLoginWorkflow />
  );
}

export default VerifiedLoginWorkflow;
