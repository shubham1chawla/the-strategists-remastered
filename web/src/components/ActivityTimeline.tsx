import { useSelector } from 'react-redux';
import { Collapse, Select, Space, Timeline } from 'antd';
import { ActivityType, State } from '../redux';
import { parseActivity } from '../utils';
import {
  BarsOutlined,
  CheckOutlined,
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
import { ReactNode, useState } from 'react';
import { Bankruptcy } from '.';

const activityTypeTree: { [key: string]: ActivityType[] } = {
  Finacials: ['BANKRUPTCY', 'BONUS', 'INVEST', 'RENT', 'TRADE'],
  Movements: ['MOVE', 'TURN'],
  Players: ['JOIN', 'KICK'],
  Specials: ['CHEAT', 'EVENT', 'JAIL', 'RESET', 'START'],
};

const getAllActivityTypes = (): ActivityType[] => {
  const types: ActivityType[] = [];
  for (const key in activityTypeTree) {
    types.push(...activityTypeTree[key]);
  }
  return types;
};

const getActivityTypeOptions = () => {
  const options = [];
  for (const key in activityTypeTree) {
    options.push({
      label: key,
      options: activityTypeTree[key].map((type) => ({
        label: type.charAt(0) + type.slice(1).toLowerCase(),
        value: type,
      })),
    });
  }
  return options;
};

export const ActivityTimeline = () => {
  const activities = useSelector((state: State) => state.activities);
  const [selectedTypes, setSelectedTypes] = useState(getAllActivityTypes());

  // Extracting filtered activities
  const filteredActivites = activities.filter(({ type }) =>
    selectedTypes.includes(type)
  );

  return (
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
            <span>
              Personalize the timeline by filtering desired Activity Types.
            </span>
          </Space>
          <Select
            className="strategists-activity__filters"
            mode="multiple"
            maxTagCount={3}
            value={selectedTypes}
            onChange={(types) => setSelectedTypes(types)}
            options={getActivityTypeOptions()}
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
  );
};

const getIcon = (type: ActivityType): ReactNode | undefined => {
  switch (type) {
    case 'BANKRUPTCY':
      return <Bankruptcy />;
    case 'END':
      return <CrownOutlined />;
    case 'INVEST':
      return <RiseOutlined />;
    case 'JOIN':
      return <UserAddOutlined />;
    case 'KICK':
      return <UserDeleteOutlined />;
    case 'MOVE':
      return <DoubleRightOutlined />;
    case 'RENT':
      return <FallOutlined />;
    case 'RESET':
      return <StopOutlined />;
    case 'START':
      return <FireOutlined />;
    case 'TURN':
      return <CheckOutlined />;
    default:
      return undefined;
  }
};
