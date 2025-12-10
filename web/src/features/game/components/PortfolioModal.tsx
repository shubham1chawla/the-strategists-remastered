import { useState } from 'react';
import { Card, Flex, Modal, Tabs } from 'antd';
import usePortfolioModal from '@game/hooks/usePortfolioModal';
import { Land, Player } from '@game/state';
import PredictionsChart from '@predictions/components/PredictionsChart';
import PredictionsChartInterpretationHelp from '@predictions/components/PredictionsChartInterpretationHelp';
import usePredictionsState from '@predictions/hooks/usePredictionsState';
import TrendsChart from '@trends/components/TrendsChart';
import TrendsChartInterpretationHelp from '@trends/components/TrendsChartInterpretationHelp';
import LandCard from './LandCard';
import PlayerCard from './PlayerCard';
import PortfolioChart from './PortfolioChart';
import PortfolioChartInterpretationHelp from './PortfolioChartInterpretationHelp';

type PortfolioModalTabKey = 'Trends' | 'Portfolio' | 'Predictions';
const defaultPortfolioModelTabKey: PortfolioModalTabKey = 'Trends';

function PortfolioModal() {
  const { perspective, node, onCancel } = usePortfolioModal();
  const predictions = usePredictionsState();
  const [tabKey, setTabKey] = useState<PortfolioModalTabKey>(
    defaultPortfolioModelTabKey,
  );

  if (!perspective || !node) {
    return null;
  }

  const playerLands =
    perspective === 'land' ? (node as Land).players : (node as Player).lands;
  const tabItems = [
    {
      key: 'Trends',
      label: 'Trends',
      children: <TrendsChart perspective={perspective} id={node.id} />,
    },
    {
      key: 'Portfolio',
      label: 'Portfolio',
      children: (
        <PortfolioChart perspective={perspective} playerLands={playerLands} />
      ),
    },
  ];

  // Adding predictions tab
  if (perspective === 'player' && predictions.length) {
    tabItems.push({
      key: 'Predictions',
      label: 'Predictions',
      children: <PredictionsChart player={node as Player} />,
    });
  }

  return (
    <Modal
      className="strategists-modal strategists-portfolio-modal"
      title={
        perspective === 'land' ? `Investments' Analysis` : 'Portfolio Analysis'
      }
      open={!!node}
      onCancel={onCancel}
      footer={null}
    >
      <Flex orientation="vertical" gap="large">
        {perspective === 'land' ? (
          <LandCard land={node as Land} />
        ) : (
          <PlayerCard player={node as Player} />
        )}
        <Card className="strategists-portfolio-modal__tabs_card">
          <Tabs
            centered
            defaultActiveKey={defaultPortfolioModelTabKey}
            size="large"
            items={tabItems}
            onChange={(key) => setTabKey(key as PortfolioModalTabKey)}
          />
        </Card>
        {tabKey === 'Trends' && (
          <TrendsChartInterpretationHelp perspective={perspective} />
        )}
        {tabKey === 'Portfolio' && (
          <PortfolioChartInterpretationHelp perspective={perspective} />
        )}
        {tabKey === 'Predictions' && (
          <PredictionsChartInterpretationHelp player={node as Player} />
        )}
      </Flex>
    </Modal>
  );
}

export default PortfolioModal;
