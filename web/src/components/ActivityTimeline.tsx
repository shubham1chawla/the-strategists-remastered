import { useSelector } from 'react-redux';
import { Tag, Timeline, Tooltip } from 'antd';
import { ActivityType, State } from '../redux';
import { parseActivity } from '../utils';
import {
  CheckOutlined,
  CrownOutlined,
  DoubleRightOutlined,
  FallOutlined,
  FireOutlined,
  ReloadOutlined,
  RiseOutlined,
  StockOutlined,
  StopOutlined,
  UserAddOutlined,
  UserDeleteOutlined,
} from '@ant-design/icons';
import { ReactNode, useState } from 'react';
import { Bankruptcy } from '.';

interface ActivityTimelineFilter {
  name: string;
  icon: ReactNode;
  types: ActivityType[];
  tooltip: string;
}

const filters: ActivityTimelineFilter[] = [
  {
    name: 'Financials',
    icon: <StockOutlined />,
    types: ['INVEST', 'RENT', 'BANKRUPTCY'],
    tooltip: 'Toggle finace-related activities',
  },
  {
    name: 'Turns & Moves',
    icon: <ReloadOutlined />,
    types: ['TURN', 'MOVE'],
    tooltip: 'Toggle movement-related activities',
  },
];

export const ActivityTimeline = () => {
  const activities = useSelector((state: State) => state.activities);
  const [disabledFilters, setDisabledFilters] = useState<
    Set<ActivityTimelineFilter>
  >(new Set());

  const toggleFilter = (filter: ActivityTimelineFilter) => {
    const updatedFilters = new Set(disabledFilters);
    if (disabledFilters.has(filter)) {
      updatedFilters.delete(filter);
    } else {
      updatedFilters.add(filter);
    }
    setDisabledFilters(updatedFilters);
  };

  // Extracting filtered activities
  const hiddenTypes = new Set();
  disabledFilters.forEach(({ types }) =>
    types.forEach((type) => hiddenTypes.add(type))
  );
  const filteredActivities = activities.filter(
    ({ type }) => !hiddenTypes.has(type)
  );

  return (
    <div className="strategists-activity">
      <div className="strategists-activity__filters">
        {filters.map((filter) => (
          <Tooltip key={filter.name} title={filter.tooltip}>
            <Tag
              key={filter.name}
              icon={filter.icon}
              className={`strategists-activity__filters__filter ${
                disabledFilters.has(filter)
                  ? ''
                  : 'strategists-activity__filters__filter-active'
              }`}
              onClick={() => toggleFilter(filter)}
            >
              {filter.name}
            </Tag>
          </Tooltip>
        ))}
      </div>
      <Timeline
        className="strategists-activity__timeline"
        items={filteredActivities.map((activity) => {
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
