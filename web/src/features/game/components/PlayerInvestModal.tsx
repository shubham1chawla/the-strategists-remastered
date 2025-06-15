import { useState } from 'react';
import {
  Button,
  Card,
  Col,
  Collapse,
  Modal,
  Row,
  Slider,
  Space,
  Statistic,
} from 'antd';
import { SliderMarks } from 'antd/es/slider';
import {
  CheckCircleOutlined,
  DollarCircleOutlined,
  ExclamationCircleOutlined,
  InfoCircleOutlined,
  PercentageOutlined,
  PieChartOutlined,
  RiseOutlined,
  SlidersOutlined,
  WalletOutlined,
} from '@ant-design/icons';
import useGame from '@game/hooks/useGame';
import InvestmentStrategy from '@game/utils/InvestmentStrategy';
import useLogin from '@login/hooks/useLogin';
import useNotifications from '@shared/hooks/useNotifications';
import LandStats from './LandStats';
import PortfolioTable from './PortfolioTable';
import axios from 'axios';

interface PlayerInvestModalProps {
  open: boolean;
  onCancel: () => void;
}

const PlayerInvestModal = ({
  open,
  onCancel,
}: Partial<PlayerInvestModalProps>) => {
  const { gameCode, player } = useLogin();
  const { lands } = useGame();
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
      console.error(error);
      errorNotification({
        message: 'Something went wrong!',
        description: 'Please refresh the page and try again.',
      });
    } finally {
      setInvesting(false);
    }
  };

  // Ensuring that if model closes, we are resetting states
  const onModalCancel = () => {
    setOwnership(0);
    setInvesting(false);
    if (onCancel) onCancel();
  };

  return (
    <Modal
      className="strategists-actions__modal"
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
      <LandStats land={land} />
      <Row>
        <Col span={12}>
          <Card bordered={false}>
            <Statistic
              title={
                <Space>
                  <PieChartOutlined />
                  Proposed Ownership
                </Space>
              }
              value={ownership}
              precision={0}
              suffix={<PercentageOutlined />}
            />
          </Card>
        </Col>
        <Col span={12}>
          <Card bordered={false}>
            <Statistic
              title={
                <Space>
                  <WalletOutlined />
                  Investment Cost
                </Space>
              }
              value={strategy.cost}
              precision={2}
              prefix={<DollarCircleOutlined />}
            />
          </Card>
        </Col>
      </Row>
      <Card className="strategists-actions__modal__slider-card">
        <Slider
          defaultValue={ownership}
          min={0}
          max={strategy.maxOfferableOwnership}
          onChangeComplete={(value) => setOwnership(value)}
          tooltip={{
            formatter: (value) => `${value}%`,
          }}
          autoFocus={true}
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
        bordered={false}
        ghost={true}
        expandIconPosition="end"
        items={[
          {
            key: '1',
            label:
              land.totalOwnership > 0 ? (
                <Space>
                  <SlidersOutlined />
                  <span>Click to check {land.name}'s investments</span>
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
    </Modal>
  );
};

const prepareSliderMarks = (strategy: InvestmentStrategy): SliderMarks => {
  const max = strategy.maxOfferableOwnership;
  const style = {
    color: 'var(--text-color)',
    marginTop: '0.5rem',
  };
  const marks: SliderMarks = {};
  [0, Math.floor(max / 2), max].forEach(
    (ownership) => (marks[ownership] = { label: `${ownership}%`, style }),
  );
  return marks;
};

export default PlayerInvestModal;
