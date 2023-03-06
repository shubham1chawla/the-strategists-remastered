import { useEffect, useState } from 'react';
import { List } from 'antd';
import axios from 'axios';

const Activity = () => {
  const [data, setData] = useState([]);

  useEffect(() => {
    axios.get('/api/activities').then((res) => {
      setData(res.data);
    });
  }, []);

  return (
    <div>
      <h2>Activity</h2>
      <div style={listContainer}>
        <List
          size="large"
          dataSource={data}
          renderItem={(item) => <List.Item style={list}>{item}</List.Item>}
        />
      </div>
    </div>
  );
};

const listContainer: React.CSSProperties = {
  paddingTop: 0,
  padding: '0px 20px',
  overflowY: 'scroll',
  maxHeight: 'calc(100vh - 172px)',
};

const list: React.CSSProperties = {
  color: '#fafafa',
  borderBlockEndColor: '#434343',
  fontSize: 16,
};

export default Activity;
