import { useCallback } from 'react';
import { useDispatch } from 'react-redux';
import { Collapse, Select, Space, Timeline } from 'antd';
import {
  BarsOutlined,
  InfoCircleOutlined,
  SettingOutlined,
} from '@ant-design/icons';
import useNotifications from '@shared/hooks/useNotifications';
import useActivitiesState from '@activities/hooks/useActivitiesState';
import {
  UpdateType,
  getSubscribableTypes,
  subscribedTypesSetted,
} from '@activities/state';
import ActivityIcon from './ActivityIcon';

function Activities() {
  const { filteredActivites, subscribedTypes } = useActivitiesState();
  const { infoNotification } = useNotifications();
  const dispatch = useDispatch();

  const formatUpdateType = (type: UpdateType): string => {
    return type.charAt(0) + type.slice(1).toLowerCase();
  };

  const setSubscribedTypes = (updateTypes: UpdateType[]) => {
    if (updateTypes.length > subscribedTypes.length) {
      const set = new Set<UpdateType>(subscribedTypes);
      const updateType = updateTypes.filter((type) => !set.has(type))[0];
      infoNotification({
        message: `Subscribed to all ${formatUpdateType(updateType)} activities!`,
      });
    } else {
      const set = new Set<UpdateType>(updateTypes);
      const updateType = subscribedTypes.filter((type) => !set.has(type))[0];
      infoNotification({
        message: `Unsubscribed from all ${formatUpdateType(updateType)} activities!`,
      });
    }
    dispatch(subscribedTypesSetted(updateTypes));
  };

  const SettingsIcon = useCallback(
    (props: any) => <SettingOutlined rotate={props.isActive ? 90 : 0} />,
    [],
  );

  return (
    <div className="strategists-activity">
      <Collapse
        bordered={false}
        expandIconPosition="end"
        accordion
        expandIcon={SettingsIcon}
        items={[
          {
            key: '1',
            label: (
              <Space>
                <BarsOutlined />
                <span>Activity Timeline</span>
              </Space>
            ),
            children: (
              <>
                <Space>
                  <InfoCircleOutlined />
                  <span>Personalize your Timeline & Notifications.</span>
                </Space>
                <Select
                  className="strategists-activity__filters"
                  mode="multiple"
                  maxTagCount={3}
                  value={subscribedTypes}
                  onChange={(types) => setSubscribedTypes(types)}
                  options={getSubscribableTypes().map((type) => ({
                    label: formatUpdateType(type),
                    value: type,
                  }))}
                />
              </>
            ),
          },
        ]}
      />
      <Timeline
        className="strategists-activity__timeline"
        items={filteredActivites.map((activity) => {
          return {
            dot: <ActivityIcon type={activity.type} />,
            children: activity.text,
          };
        })}
      />
    </div>
  );
}

export default Activities;
