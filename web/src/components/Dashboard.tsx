import React from 'react';
import { Typography } from 'antd';

const { Text } = Typography;

type DashboardProps = {
  user: string;
};

const Dashboard = ({ user }: DashboardProps) => {
  return (
    <div>
      <h5>
        Hello <Text mark>{user}</Text>! This is Dashboard Component
      </h5>
    </div>
  );
};

export default Dashboard;
