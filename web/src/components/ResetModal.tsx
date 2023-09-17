import { StopOutlined } from '@ant-design/icons';
import { Alert, Button, Checkbox, Col, Divider, Modal, Row } from 'antd';
import axios from 'axios';
import { useState } from 'react';

export interface ResetModalProps {
  open?: boolean;
  onCancel?: () => void;
}

export const ResetModal = (props: ResetModalProps) => {
  const [confirmed, setConfirmed] = useState(false);

  const { open, onCancel } = props;
  if (!open || !onCancel) {
    return null;
  }

  const reset = () => {
    axios.delete('/api/game');
    cancel();
  };

  const cancel = () => {
    setConfirmed(false);
    onCancel();
  };

  return (
    <Modal
      open={open}
      onCancel={cancel}
      onOk={reset}
      title="Reset The Strategists"
      okText={
        <>
          <StopOutlined /> Reset
        </>
      }
      footer={
        <Row align="middle">
          <Col span={1}>
            <Checkbox
              value={confirmed}
              onChange={({ target }) => setConfirmed(target.checked)}
            />
          </Col>
          <Col span={11}>
            <span>I confirm to reset The Strategists.</span>
          </Col>
          <Col span={12}>
            <Button onClick={cancel}>Cancel</Button>
            <Button type="primary" disabled={!confirmed} onClick={reset}>
              Reset
            </Button>
          </Col>
        </Row>
      }
    >
      <Divider />
      <Alert
        type="error"
        message="Do you want to reset The Strategists?"
        description="This action will permanently reset the game. Are you sure you want to continue?"
        showIcon
        icon={<StopOutlined />}
      />
      <Divider />
    </Modal>
  );
};
