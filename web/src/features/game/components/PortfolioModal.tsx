import { Modal, Tabs } from 'antd';
import { Land, Player } from '@game/state';
import PredictionsChart from '@predictions/components/PredictionsChart';
import TrendsChart from '@trends/components/TrendsChart';
import LandStats from './LandStats';
import PlayerStats from './PlayerStats';
import PortfolioChart from './PortfolioChart';

export interface PortfolioModalProps {
  player: Player;
  land: Land;
  open: boolean;
  onCancel: () => void;
}

const PortfolioModal = (props: Partial<PortfolioModalProps>) => {
  const { open, onCancel, player, land } = props;
  if (!open || !onCancel || (!!land && !!player) || (!land && !player)) {
    return null;
  }
  const perspective = land ? 'land' : 'player';
  const playerLands = land ? land.players : player ? player.lands : [];
  const id = land ? land.id : player ? player.id : -1;

  const tabItems = [
    {
      key: '1',
      label: 'Trends',
      children: (
        <TrendsChart perspective={perspective} id={id} showHelp={true} />
      ),
    },
    {
      key: '2',
      label: 'Portfolio',
      children: (
        <PortfolioChart
          perspective={perspective}
          playerLands={playerLands}
          showHelp={true}
        />
      ),
    },
  ];

  // Adding predictions tab
  if (perspective === 'player' && !!player) {
    tabItems.push({
      key: '3',
      label: 'Predictions',
      children: <PredictionsChart player={player} showHelp={true} />,
    });
  }

  return (
    <Modal
      title={land ? `Investments' Analysis` : 'Portfolio Analysis'}
      open={open}
      onCancel={onCancel}
      footer={null}
    >
      {land ? (
        <LandStats land={land} />
      ) : player ? (
        <PlayerStats player={player} />
      ) : null}
      <Tabs centered defaultActiveKey="1" size="large" items={tabItems} />
    </Modal>
  );
};

export default PortfolioModal;
