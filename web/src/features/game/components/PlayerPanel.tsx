import { useEffect, useState } from 'react';
import { Badge, Space, Tabs, TabsProps } from 'antd';
import useNotifications from '@shared/hooks/useNotifications';
import Activities from '@activities/components/Activities';
import Advices from '@advices/components/Advices';
import useAdvicesState from '@advices/hooks/useAdvicesState';
import useGameState from '@game/hooks/useGameState';
import useLoginState from '@login/hooks/useLoginState';
import Lobby from './Lobby';
import NavigationBar from './NavigationBar';
import PlayerActionsPanel from './PlayerActionsPanel';
import PlayerStats from './PlayerStats';

type PlayerPanelTabKey = 'LOBBY' | 'ACTIVITIES' | 'ADVICE';

function PlayerPanel() {
  const { player } = useLoginState();
  const { game } = useGameState();
  const { playerAdvices, unreadCount, markAdvicesRead } = useAdvicesState();
  const { errorNotification } = useNotifications();
  const [activeKey, setActiveKey] = useState<PlayerPanelTabKey>('LOBBY');

  // Switching tabs when game's state changes
  useEffect(() => {
    setActiveKey(game.state === 'ACTIVE' ? 'ACTIVITIES' : 'LOBBY');
  }, [game.state]);

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
        markAdvicesRead().catch(() => {
          errorNotification({
            message: 'Something went wrong!',
            description:
              'Unable to mark your advices as read! If this problem persists, please contact the developers.',
          });
        });
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
}

export default PlayerPanel;
