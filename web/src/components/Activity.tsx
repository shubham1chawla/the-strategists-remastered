import { useSelector } from 'react-redux';
import { List } from 'antd';
import { CoffeeOutlined } from '@ant-design/icons';
import { State } from '../redux';

export const Activity = () => {
  const activities = useSelector((state: State) => state.activities);

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
