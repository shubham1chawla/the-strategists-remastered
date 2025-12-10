import { Timeline } from 'antd';
import useActivitiesState from '@activities/hooks/useActivitiesState';
import ActivityIcon from './ActivityIcon';

function Activities() {
  const { activities } = useActivitiesState();
  return (
    <Timeline
      className="strategists-activities"
      items={activities.map((activity) => ({
        icon: <ActivityIcon type={activity.type} />,
        content: activity.text,
      }))}
    />
  );
}

export default Activities;
