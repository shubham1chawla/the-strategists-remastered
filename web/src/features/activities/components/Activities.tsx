import { Collapse, Select, Space, Timeline } from 'antd';
import { useDispatch } from 'react-redux';
import {
  BarsOutlined,
  InfoCircleOutlined,
  SettingOutlined,
} from '@ant-design/icons';
import useActivities from '@activities/hooks/useActivities';
import {
  UpdateType,
  getSubscribableTypes,
  subscribedTypesSetted,
} from '@activities/state';
import useNotifications from '@shared/hooks/useNotifications';
import ActivityIcon from './ActivityIcon';
import parseActivity from '@activities/utils/parseActivity';

const Activities = () => {
  const { filteredActivites, subscribedTypes } = useActivities();
  const { contextHolder, ...api } = useNotifications();
  const dispatch = useDispatch();

  const formatUpdateType = (type: UpdateType): string => {
    return type.charAt(0) + type.slice(1).toLowerCase();
  };

  const setSubscribedTypes = (types: UpdateType[]) => {
    if (types.length > subscribedTypes.length) {
      const set = new Set<UpdateType>(subscribedTypes);
      const type = types.filter((type) => !set.has(type))[0];
      api.info({
        message: `Subscribed to all ${formatUpdateType(type)} activities!`,
      });
    } else {
      const set = new Set<UpdateType>(types);
      const type = subscribedTypes.filter((type) => !set.has(type))[0];
      api.info({
        message: `Unsubscribed from all ${formatUpdateType(type)} activities!`,
      });
    }
    dispatch(subscribedTypesSetted(types));
  };

  return (
    <>
      {contextHolder}
      <div className="strategists-activity">
        <Collapse
          bordered={false}
          expandIconPosition="end"
          accordion={true}
          expandIcon={(props) => (
            <SettingOutlined rotate={props.isActive ? 90 : 0} />
          )}
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
              children: parseActivity(activity),
            };
          })}
        />
      </div>
    </>
  );
};

export default Activities;
