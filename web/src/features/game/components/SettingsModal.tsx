import { Flex, Modal, Select, Space } from 'antd';
import { NotificationOutlined, SettingOutlined } from '@ant-design/icons';
import useActivitiesState from '@activities/hooks/useActivitiesState';
import { getSubscribableTypes } from '@activities/state';

function SetSubscribedTypes() {
  const { subscribedTypes, setSubscribedTypes, formatUpdateType } =
    useActivitiesState();

  return (
    <Flex orientation="vertical" gap="small">
      <Space>
        <NotificationOutlined />
        <span>Personalize Subscribed Notifications</span>
      </Space>
      <Select
        mode="multiple"
        placeholder="Select notification types"
        maxTagCount={4}
        value={subscribedTypes}
        onChange={(types) => setSubscribedTypes(types)}
        options={getSubscribableTypes().map((type) => ({
          label: formatUpdateType(type),
          value: type,
        }))}
      />
    </Flex>
  );
}

interface SettingsModalProps {
  open?: boolean;
  onCancel?: () => void;
}

function SettingsModal({ open, onCancel }: SettingsModalProps) {
  return (
    <Modal
      className="strategists-modal"
      title={
        <Space>
          <SettingOutlined />
          Settings
        </Space>
      }
      open={open}
      onCancel={onCancel}
      footer={null}
    >
      <Flex orientation="vertical" gap="large">
        <SetSubscribedTypes />
      </Flex>
    </Modal>
  );
}

export default SettingsModal;
