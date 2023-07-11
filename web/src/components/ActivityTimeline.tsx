import { useSelector } from 'react-redux';
import { Timeline } from 'antd';
import { Activity, State, parseActivity } from '../redux';
import {
  CheckOutlined,
  DoubleRightOutlined,
  FireOutlined,
  StockOutlined,
  UserAddOutlined,
} from '@ant-design/icons';
import { ReactNode } from 'react';

export const ActivityTimeline = () => {
  const activities = useSelector((state: State) => state.activities);
  return (
    <Timeline
      className="strategists-activity"
      items={activities.map((activity) => {
        return {
          dot: getIcon(activity),
          children: parseActivity(activity),
        };
      })}
    />
  );
};

const getIcon = ({ type }: Activity): ReactNode | undefined => {
  switch (type) {
    case 'INVEST':
      return <StockOutlined />;
    case 'JOIN':
      return <UserAddOutlined />;
    case 'MOVE':
      return <DoubleRightOutlined />;
    case 'START':
      return <FireOutlined />;
    case 'TURN':
      return <CheckOutlined />;
    default:
      return undefined;
  }
};
