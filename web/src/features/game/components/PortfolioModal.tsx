import { Modal, Tabs } from 'antd';
import { Land, Player } from '@game/state';
import PredictionsChart from '@predictions/components/PredictionsChart';
import TrendsChart from '@trends/components/TrendsChart';
import LandStats from './LandStats';
import PlayerStats from './PlayerStats';
import PortfolioChart from './PortfolioChart';

export interface PortfolioModalProps {
  open: boolean;
  onCancel: () => void;
  perspective: 'land' | 'player';
  node: Land | Player;
}

function PortfolioModal({
  open,
  onCancel,
  perspective,
  node,
}: Partial<PortfolioModalProps>) {
  if (!open || !perspective || !node) {
    return null;
  }

  const playerLands =
    perspective === 'land' ? (node as Land).players : (node as Player).lands;
  const tabItems = [
    {
      key: '1',
      label: 'Trends',
      children: <TrendsChart perspective={perspective} id={node.id} showHelp />,
    },
    {
      key: '2',
      label: 'Portfolio',
      children: (
        <PortfolioChart
          perspective={perspective}
          playerLands={playerLands}
          showHelp
        />
      ),
    },
  ];

  // Adding predictions tab
  if (perspective === 'player') {
    tabItems.push({
      key: '3',
      label: 'Predictions',
      children: <PredictionsChart player={node as Player} showHelp />,
    });
  }

  return (
    <Modal
      title={
        perspective === 'land' ? `Investments' Analysis` : 'Portfolio Analysis'
      }
      open={open}
      onCancel={onCancel}
      footer={null}
    >
      {perspective === 'land' ? (
        <LandStats land={node as Land} />
      ) : (
        <PlayerStats player={node as Player} />
      )}
      <Tabs centered defaultActiveKey="1" size="large" items={tabItems} />
    </Modal>
  );
}

export default PortfolioModal;
