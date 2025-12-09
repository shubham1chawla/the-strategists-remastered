import { useState } from 'react';
import { Alert, Button, Checkbox, Modal, Row, Space } from 'antd';
import { StopOutlined } from '@ant-design/icons';
import axios from 'axios';

interface ResetModalProps {
  gameCode: string;
  open: boolean;
  onCancel: () => void;
}

function ResetModal(props: ResetModalProps) {
  const [checked, setChecked] = useState(false);
  const { open, onCancel, gameCode } = props;

  const cancel = () => {
    setChecked(false);
    onCancel();
  };

  const reset = async () => {
    await axios.delete(`/api/games/${gameCode}`);
    cancel();
  };

  return (
    <Modal
      className="strategists-modal"
      open={open}
      onCancel={cancel}
      onOk={reset}
      title="Reset The Strategists"
      footer={
        <Row justify="space-between" align="middle">
          <Space>
            <Checkbox
              checked={checked}
              onChange={({ target }) => setChecked(target.checked)}
            />
            <span>Authorize resetting The Strategists.</span>
          </Space>
          <Space>
            <Button onClick={cancel}>Cancel</Button>
            <Button type="primary" disabled={!checked} onClick={reset}>
              Reset
            </Button>
          </Space>
        </Row>
      }
    >
      <Alert
        type="error"
        title="Do you want to reset The Strategists?"
        description="This action will permanently reset the game. Are you sure you want to continue?"
        icon={<StopOutlined />}
        showIcon
      />
    </Modal>
  );
}

export default ResetModal;
