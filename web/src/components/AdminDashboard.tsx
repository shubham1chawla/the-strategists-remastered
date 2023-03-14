import React from 'react';
import { Tabs } from 'antd';
import type { TabsProps } from 'antd';

const AdminDashboard = () => {
  const onChange = (key: string) => {
    console.log(key);
  };

  const items: TabsProps['items'] = [
    {
      key: '1',
      label: `Lobby`,
      children: `Content of Tab Lobby`,
    },
    {
      key: '2',
      label: `Feed`,
      children: `Content of Tab Feed`,
    },
    {
      key: '3',
      label: `Events`,
      children: `Content of Tab Events`,
    },
  ];

  return (
    <div style={adminDashboardContainer}>
      <Tabs centered defaultActiveKey="1" items={items} onChange={onChange} />
    </div>
  );
};

const adminDashboardContainer: React.CSSProperties = {
  height: '100vh',
  display: 'flex',
  flexDirection: 'column',
};

export default AdminDashboard;
