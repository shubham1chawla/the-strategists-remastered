import { useEffect, useState } from 'react';
import { List } from 'antd';
import { CoffeeOutlined } from '@ant-design/icons';
import axios from 'axios';

export const Activity = () => {
  const [data, setData] = useState([]);

  useEffect(() => {
    axios.get('/api/activities').then((res) => {
      setData(res.data);
    });
  }, []);

  return (
    <div className="strategists-activity">
      <header>
        <CoffeeOutlined /> Activity
      </header>
      <List
        className="strategists-list"
        size="large"
        dataSource={data}
        renderItem={(feed) => (
          <List.Item className="strategists-list__item">{feed}</List.Item>
        )}
      />
    </div>
  );
};
