import { useSelector } from 'react-redux';
import { Timeline } from 'antd';
import { Activity, State } from '../redux';
import { parseActivity } from '../utils';
import {
  CheckOutlined,
  CrownOutlined,
  DoubleRightOutlined,
  FallOutlined,
  FireOutlined,
  RiseOutlined,
  UserAddOutlined,
  UserDeleteOutlined,
} from '@ant-design/icons';
import { ReactNode } from 'react';
import { Bankruptcy } from '.';

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
    case 'START':
      return <FireOutlined />;
    case 'TURN':
      return <CheckOutlined />;
    default:
      return undefined;
  }
};
