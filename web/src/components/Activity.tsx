import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { List } from 'antd';
import { CoffeeOutlined } from '@ant-design/icons';
import { ActivityActions, ActivityState } from '../redux';
import axios from 'axios';

export const Activity = () => {
  const activities: ActivityState = useSelector(
    (state: any) => state.activities
  );
  const dispatch = useDispatch();

  useEffect(() => {
    if (activities.length === 0) {
      axios
        .get('/api/activities')
        .then(({ data }) => dispatch(ActivityActions.setActivities(data)));
    }
  }, [dispatch, activities.length]);

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
