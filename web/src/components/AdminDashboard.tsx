import { CSSProperties } from 'react';
import { Tabs } from 'antd';
import type { TabsProps } from 'antd';
import { Lobby } from '.';

export const AdminDashboard = () => {
  const items: TabsProps['items'] = [
    {
      key: '1',
      label: `Lobby`,
      children: <Lobby />,
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
      <Tabs centered defaultActiveKey="1" size="middle" items={items} />
    </div>
  );
};

const adminDashboardContainer: CSSProperties = {
  height: '100vh',
  display: 'flex',
  flexDirection: 'column',
  backgroundColor: '#191a24',
};
