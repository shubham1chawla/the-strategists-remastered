import { useSelector } from 'react-redux';
import { List } from 'antd';
import { State } from '../redux';
import { LineOutlined } from '@ant-design/icons';

export const Activity = () => {
  const activities = useSelector((state: State) => state.activities);

  return (
    <List
      className="strategists-activity"
      size="large"
      dataSource={activities}
      renderItem={(activity) => (
        <List.Item>
          <LineOutlined /> {activity}
        </List.Item>
      )}
    />
  );
};
