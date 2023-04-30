import { useSelector } from 'react-redux';
import { List } from 'antd';
import { CoffeeOutlined } from '@ant-design/icons';
import { ActivityState } from '../redux';

export const Activity = () => {
  const activities: ActivityState = useSelector(
    (state: any) => state.activities
  );

  return (
    <div className="strategists-activity">
      <header>
        <CoffeeOutlined /> Activities
      </header>
      <List
        className="strategists-list"
        size="large"
        dataSource={activities}
        renderItem={(activity) => (
          <List.Item className="strategists-list__item">{activity}</List.Item>
        )}
      />
    </div>
  );
};
