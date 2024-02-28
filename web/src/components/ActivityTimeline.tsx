import { ReactNode } from 'react';
import { Collapse, Select, Space, Timeline, notification } from 'antd';
import { useDispatch, useSelector } from 'react-redux';
import {
  BarsOutlined,
  BulbOutlined,
  CheckOutlined,
  ClockCircleOutlined,
  CrownOutlined,
  DoubleRightOutlined,
  FallOutlined,
  FireOutlined,
  InfoCircleOutlined,
  RiseOutlined,
  SettingOutlined,
  StopOutlined,
  UserAddOutlined,
  UserDeleteOutlined,
} from '@ant-design/icons';
import { Bankruptcy } from '.';
import {
  ActivityActions,
  State,
  UpdateType,
  getSubscribableTypes,
} from '../redux';
import { parseActivity } from '../utils';

export const ActivityTimeline = () => {
  const { activities, subscribedTypes } = useSelector(
    (state: State) => state.activity
  );
  const dispatch = useDispatch();
  const [api, contextHolder] = notification.useNotification({ maxCount: 3 });

  // Extracting filtered activities
  const filteredActivites = activities.filter(({ type }) =>
    subscribedTypes.includes(type)
  );

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
    dispatch(ActivityActions.setSubscribedTypes(types));
  };

  return (
    <>
      {contextHolder}
      <div className="strategists-activity">
        <Collapse
          size="large"
          bordered={false}
          expandIconPosition="end"
          accordion={true}
          expandIcon={(props) => (
            <SettingOutlined rotate={props.isActive ? 90 : 0} />
          )}
        >
          <Collapse.Panel
            key="1"
            header={
              <Space>
                <BarsOutlined />
                <span>Activity Timeline</span>
              </Space>
            }
          >
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
          </Collapse.Panel>
        </Collapse>
        <Timeline
          className="strategists-activity__timeline"
          items={filteredActivites.map((activity) => {
            return {
              dot: getIcon(activity.type),
              children: parseActivity(activity),
            };
          })}
        />
      </div>
    </>
  );
};

const getIcon = (type: UpdateType): ReactNode | undefined => {
  switch (type) {
    case 'BANKRUPTCY':
      return <Bankruptcy />;
    case 'INVEST':
      return <RiseOutlined />;
    case 'JOIN':
      return <UserAddOutlined />;
    case 'KICK':
      return <UserDeleteOutlined />;
    case 'MOVE':
      return <DoubleRightOutlined />;
    case 'PREDICTION':
      return <BulbOutlined />;
    case 'RENT':
      return <FallOutlined />;
    case 'RESET':
      return <StopOutlined />;
    case 'SKIP':
      return <ClockCircleOutlined />;
    case 'START':
      return <FireOutlined />;
    case 'TURN':
      return <CheckOutlined />;
    case 'WIN':
      return <CrownOutlined />;
    default:
      return undefined;
  }
};
