import React, { CSSProperties } from 'react';
import { Tabs } from 'antd';
import type { TabsProps } from 'antd';
import AdminLobby from './AdminLobby';

const AdminDashboard = () => {
  const onChange = (key: string) => {
    console.log(key);
  };

  const items: TabsProps['items'] = [
    {
      key: '1',
      label: `Lobby`,
      children: <AdminLobby />,
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
      <Tabs
        centered
        defaultActiveKey="1"
        size="middle"
        items={items}
        onChange={onChange}
      />
    </div>
  );
};

const adminDashboardContainer: CSSProperties = {
  height: '100vh',
  display: 'flex',
  flexDirection: 'column',
  backgroundColor: '#191a24',
};

export default AdminDashboard;
