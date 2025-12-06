import { useState } from 'react';
import { Button, Card, Collapse, Flex, Modal, Row, Slider, Space } from 'antd';
import { SliderMarks } from 'antd/es/slider';
import {
  CheckCircleOutlined,
  ExclamationCircleOutlined,
  InfoCircleOutlined,
  RiseOutlined,
  SlidersOutlined,
} from '@ant-design/icons';
import axios from 'axios';
import useNotifications from '@shared/hooks/useNotifications';
import useGameState from '@game/hooks/useGameState';
import InvestmentStrategy from '@game/utils/InvestmentStrategy';
import useLoginState from '@login/hooks/useLoginState';
import LandCard from './LandCard';
import PortfolioTable from './PortfolioTable';

const prepareSliderMarks = (strategy: InvestmentStrategy): SliderMarks => {
  const max = strategy.maxOfferableOwnership;
  const style = {
    color: 'var(--text-color)',
    marginTop: '0.5rem',
  };
  const marks: SliderMarks = {};
  [0, Math.floor(max / 2), max].forEach((ownership) => {
    marks[ownership] = { label: `${ownership}%`, style };
  });
  return marks;
};

interface PlayerInvestModalProps {
  open: boolean;
  onCancel: () => void;
}

function PlayerInvestModal({
  open,
  onCancel,
}: Partial<PlayerInvestModalProps>) {
  const { gameCode, player } = useLoginState();
  const { lands } = useGameState();
  const { errorNotification } = useNotifications();
  const [ownership, setOwnership] = useState(0);
  const [investing, setInvesting] = useState(false);

  // Validating props
  if (!open || !gameCode || !player) {
    return null;
  }

  // Closing the modal if player doesn't have the turn
  if (!player.turn) {
    if (onCancel) onCancel();
    return null;
  }

  // Current land of the player
  const land = lands[player.index];

  // Setting up investment strategy
  const strategy = new InvestmentStrategy(player, land, ownership);

  // Ensuring that if model closes, we are resetting states
  const onModalCancel = () => {
    setOwnership(0);
    setInvesting(false);
    if (onCancel) onCancel();
  };

  const onInvest = async () => {
    setInvesting(true);
    try {
      // Investing in the land
      await axios.post(`/api/games/${gameCode}/players/${player.id}/lands`, {
        landId: land.id,
        ownership,
      });

      // Ending player's turn after investing in any land
      await axios.put(`/api/games/${gameCode}/turn`);

      // Closing the modal
      onModalCancel();
    } catch (error) {
      errorNotification({
        title: 'Something went wrong!',
        description: 'Please refresh the page and try again.',
      });
    } finally {
      setInvesting(false);
    }
  };

  return (
    <Modal
      className="strategists-modal strategists-invest-modal"
      title="Investment Strategy"
      open={!!open}
      onCancel={onModalCancel}
      footer={
        <Row justify="space-between" wrap={false}>
          <Space>
            {strategy.maxOfferableOwnership !== strategy.availableOwnership ? (
              <>
                <ExclamationCircleOutlined />
                <span>
                  Offer capped at {strategy.maxOfferableOwnership}% due to low
                  cash!
                </span>
              </>
            ) : (
              ''
            )}
          </Space>
          <Space>
            <Button onClick={onModalCancel}>Cancel</Button>
            <Button
              type="primary"
              disabled={!strategy.feasible || investing}
              icon={<RiseOutlined />}
              onClick={onInvest}
              loading={investing}
            >
              Invest
            </Button>
          </Space>
        </Row>
      }
    >
      <Flex orientation="vertical" gap="large">
        <LandCard
          land={land}
          propsedOwnership={ownership}
          investmentCost={strategy.cost}
        />
        <Card>
          <Slider
            defaultValue={ownership}
            min={0}
            max={strategy.maxOfferableOwnership}
            onChangeComplete={(value) => setOwnership(value)}
            tooltip={{
              formatter: (value) => `${value}%`,
            }}
            autoFocus
            marks={prepareSliderMarks(strategy)}
          />
          <Row justify="center">
            <Space>
              <InfoCircleOutlined />
              Use this slider to adjust your investment offer.
            </Space>
          </Row>
        </Card>
        <Collapse
          expandIconPlacement="end"
          items={[
            {
              key: '1',
              label:
                land.totalOwnership > 0 ? (
                  <Space>
                    <SlidersOutlined />
                    <span>Click to check {land.name}&apos;s investments</span>
                  </Space>
                ) : (
                  <Space>
                    <CheckCircleOutlined />
                    <span>No investments in {land.name}!</span>
                  </Space>
                ),
              children: (
                <PortfolioTable perspective="land" playerLands={land.players} />
              ),
            },
          ]}
        />
      </Flex>
    </Modal>
  );
}

export default PlayerInvestModal;
