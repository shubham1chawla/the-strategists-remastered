import { useEffect, useState } from 'react';
import { Badge, Button, Flex, Space, Tabs, TabsProps } from 'antd';
import { SettingOutlined } from '@ant-design/icons';
import useNotifications from '@shared/hooks/useNotifications';
import Activities from '@activities/components/Activities';
import Advices from '@advices/components/Advices';
import useAdvicesState from '@advices/hooks/useAdvicesState';
import useGameState from '@game/hooks/useGameState';
import useLoginState from '@login/hooks/useLoginState';
import Lobby from './Lobby';
import LobbyPlayerCard from './LobbyPlayerCard';
import NavigationBar from './NavigationBar';
import PlayerActionsPanel from './PlayerActionsPanel';
import SettingsModal from './SettingsModal';

type PlayerPanelTabKey = 'LOBBY' | 'ACTIVITIES' | 'ADVICE';

function PlayerPanel() {
  const { player } = useLoginState();
  const { game } = useGameState();
  const { playerAdvices, unreadCount, markAdvicesRead } = useAdvicesState();
  const { errorNotification } = useNotifications();
  const [activeKey, setActiveKey] = useState<PlayerPanelTabKey>('LOBBY');
  const [isSettingsModalOpen, setOpenSettingsModal] = useState(false);

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
            title: 'Something went wrong!',
            description:
              'Unable to mark your advices as read! If this problem persists, please contact the developers.',
          });
        });
      }
      return key as PlayerPanelTabKey;
    });
  };

  return (
    <Flex
      className="strategists-player-panel"
      orientation="vertical"
      flex="1 1 auto 1"
    >
      <NavigationBar />
      <LobbyPlayerCard
        player={player}
        extra={
          <Button
            shape="circle"
            type="text"
            icon={<SettingOutlined />}
            onClick={() => setOpenSettingsModal(true)}
          />
        }
      />
      <Tabs
        centered
        defaultActiveKey="LOBBY"
        activeKey={activeKey}
        onChange={onTabChange}
        size="large"
        items={items}
      />
      <PlayerActionsPanel />
      <SettingsModal
        open={isSettingsModalOpen}
        onCancel={() => setOpenSettingsModal(false)}
      />
    </Flex>
  );
}

export default PlayerPanel;
