import { useEffect, useState } from 'react';
import { Badge, Space, Tabs, TabsProps } from 'antd';
import Activities from '@activities/components/Activities';
import Advices from '@advices/components/Advices';
import useAdvices from '@advices/hooks/useAdvices';
import PlayerActionsPanel from './PlayerActionsPanel';
import Lobby from './Lobby';
import NavigationBar from './NavigationBar';
import PlayerStats from './PlayerStats';
import useGame from '@game/hooks/useGame';
import useLogin from '@login/hooks/useLogin';

type PlayerPanelTabKey = 'LOBBY' | 'ACTIVITIES' | 'ADVICE';

const PlayerPanel = () => {
  const { player } = useLogin();
  const { state } = useGame();
  const { playerAdvices, unreadCount, markAdvicesRead } = useAdvices();
  const [activeKey, setActiveKey] = useState<PlayerPanelTabKey>('LOBBY');

  // Switching tabs when game's state changes
  useEffect(() => {
    setActiveKey(state === 'ACTIVE' ? 'ACTIVITIES' : 'LOBBY');
  }, [state]);

  // Validation
  if (!player) {
    return null;
  }

  // Creating tabs
  const items: TabsProps['items'] = [
    {
      key: 'LOBBY',
      label: 'Lobby',
      children: <Lobby />,
    },
    {
      key: 'ACTIVITIES',
      label: 'Activities',
      children: <Activities />,
    },
  ];

  // Adding Advice tab if the feature is enabled
  if (playerAdvices.length) {
    items.push({
      key: 'ADVICE',
      label: (
        <Space align="center">
          <span>Advices</span>
          <Badge count={unreadCount} offset={[0, -2]} status="default" />
        </Space>
      ),
      children: <Advices />,
    });
  }

  const onTabChange = (key: string) => {
    setActiveKey((prev) => {
      if (prev === 'ADVICE') {
        markAdvicesRead();
      }
      return key as PlayerPanelTabKey;
    });
  };

  return (
    <>
      <NavigationBar />
      <PlayerStats player={player} showRemainingSkipsCount />
      <Tabs
        centered
        defaultActiveKey="LOBBY"
        activeKey={activeKey}
        onChange={onTabChange}
        size="large"
        items={items}
      />
      <PlayerActionsPanel />
    </>
  );
};

export default PlayerPanel;
