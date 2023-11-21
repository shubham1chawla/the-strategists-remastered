import { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Land, Player, State, UserActions } from '../redux';
import { InvestmentStrategy } from '../utils';
import {
  Alert,
  Button,
  Card,
  Checkbox,
  Col,
  Collapse,
  Divider,
  Modal,
  Row,
  Slider,
  Space,
  Statistic,
  Tag,
} from 'antd';
import { SliderMarks } from 'antd/es/slider';
import {
  CheckCircleOutlined,
  CrownOutlined,
  DollarCircleOutlined,
  ExclamationCircleOutlined,
  HeartOutlined,
  InfoCircleOutlined,
  LogoutOutlined,
  PercentageOutlined,
  PieChartOutlined,
  RiseOutlined,
  SlidersOutlined,
  StopFilled,
  StopOutlined,
  WalletOutlined,
} from '@ant-design/icons';
import {
  Confetti,
  LandStats,
  Logo,
  PlayerStats,
  Portfolio,
  TabularPortfolio,
  VisualPortfolio,
} from '.';
import axios from 'axios';

export interface BaseModalProps {
  open: boolean;
  onCancel: () => void;
}

/**
 * -----  INVEST MODAL BELOW  -----
 */

const prepareSliderMarks = (strategy: InvestmentStrategy): SliderMarks => {
  const max = strategy.maxOfferableOwnership;
  const style = {
    color: 'var(--text-color)',
    marginTop: '0.5rem',
  };
  const marks: SliderMarks = {};
  [0, Math.floor(max / 2), max].forEach(
    (ownership) => (marks[ownership] = { label: `${ownership}%`, style })
  );
  return marks;
};

export interface PlayerInvestModalProps extends BaseModalProps {
  player: Player;
  land: Land;
  title: string;
}

export const PlayerInvestModal = (props: Partial<PlayerInvestModalProps>) => {
  const { open, player, land, title, onCancel } = props;
  const [ownership, setOwnership] = useState(0);
  if (!open || !player || !land || !title || !onCancel) {
    return null;
  }

  // Setting up investment strategy
  const strategy = new InvestmentStrategy(player, land, ownership);

  const invest = async () => {
    await axios.post(`/api/players/${player.id}/lands`, {
      landId: land.id,
      ownership,
    });
    setOwnership(0);
    onCancel();

    // Ending player's turn after investing in any land
    axios.put('/api/game');
  };

  return (
    <Modal
      className="strategists-actions__modal"
      title="Investment Strategy"
      open={!!open}
      onCancel={onCancel}
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
            <Button onClick={onCancel}>Cancel</Button>
            <Button
              type="primary"
              disabled={!strategy.feasible}
              icon={<RiseOutlined />}
              onClick={invest}
            >
              Invest
            </Button>
          </Space>
        </Row>
      }
    >
      <Row>
        <Col span={24}>
          <Divider>
            <Tag icon={<RiseOutlined />}>{title}</Tag>
          </Divider>
        </Col>
      </Row>
      <Row>
        <Col span={12}>
          <Card bordered={false}>
            <Statistic
              title={
                <Space>
                  <PieChartOutlined />
                  Remaining Ownership
                </Space>
              }
              value={strategy.availableOwnership - ownership}
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
                  Remaining Cash
                </Space>
              }
              value={player.cash - strategy.cost}
              precision={2}
              prefix={<DollarCircleOutlined />}
            />
          </Card>
        </Col>
      </Row>
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
          onAfterChange={(value) => setOwnership(value)}
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
        size="large"
        bordered={false}
        ghost={true}
        expandIconPosition="end"
      >
        <Collapse.Panel
          key="1"
          header={
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
            )
          }
        >
          <TabularPortfolio perspective="land" playerLands={land.players} />
        </Collapse.Panel>
      </Collapse>
    </Modal>
  );
};

/**
 * -----  PORTFOLIO MODAL BELOW  -----
 */

export interface PortfolioModalProps extends BaseModalProps {
  player: Player;
  land: Land;
}

export const PortfolioModal = (props: Partial<PortfolioModalProps>) => {
  const { open, onCancel, player, land } = props;
  if (!open || !onCancel || (!!land && !!player) || (!land && !player)) {
    return null;
  }
  const perspective = land ? 'land' : 'player';
  const playerLands = land ? land.players : player ? player.lands : [];

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
      <Portfolio perspective={perspective} playerLands={playerLands} />
    </Modal>
  );
};

/**
 * -----  RESET MODAL BELOW  -----
 */

export interface ResetModalProps extends BaseModalProps {
  // No additional fields
}

export const ResetModal = (props: ResetModalProps) => {
  const [checked, setChecked] = useState(false);
  const { open, onCancel } = props;

  const reset = async () => {
    await axios.delete('/api/game');
    cancel();
  };

  const cancel = () => {
    setChecked(false);
    onCancel();
  };

  return (
    <Modal
      open={open}
      onCancel={cancel}
      onOk={reset}
      title="Reset The Strategists"
      okText={
        <>
          <StopOutlined /> Reset
        </>
      }
      footer={
        <Row justify="space-between" align="middle">
          <Space>
            <Checkbox
              checked={checked}
              onChange={({ target }) => setChecked(target.checked)}
            />
            <span>Authorize resetting The Strategists.</span>
          </Space>
          <Space>
            <Button onClick={cancel}>Cancel</Button>
            <Button type="primary" disabled={!checked} onClick={reset}>
              Reset
            </Button>
          </Space>
        </Row>
      }
    >
      <Divider />
      <Alert
        banner
        type="error"
        message="Do you want to reset The Strategists?"
        description="This action will permanently reset the game. Are you sure you want to continue?"
        showIcon
        icon={<StopOutlined />}
      />
      <Divider />
    </Modal>
  );
};

/**
 * -----  WIN MODAL BELOW  -----
 */

export const WinModal = () => {
  const { user, lobby } = useSelector((state: State) => state);
  const { type } = user;
  const { players, state } = lobby;

  const [showResetModal, setShowResetModal] = useState(false);

  const dispatch = useDispatch();
  const closeResetModal = () => setShowResetModal(false);
  const openResetModal = () => setShowResetModal(true);

  // determining whether to show win modal
  const activePlayers = players.filter((player) => player.state === 'ACTIVE');
  const player =
    state === 'ACTIVE' && activePlayers.length === 1
      ? activePlayers[0]
      : undefined;
  if (!player) return null;

  return (
    <>
      <Confetti type="multiple" />
      <Modal
        open={!!player}
        onCancel={closeResetModal}
        closable={false}
        maskClosable={false}
        title={
          <Row justify="space-between" align="middle">
            <Logo />
            <Tag icon={<CrownOutlined />}>Winner</Tag>
          </Row>
        }
        footer={
          <Row justify="space-between" align="middle">
            {type === 'ADMIN' ? (
              <>
                <Space>
                  <InfoCircleOutlined />
                  Reset the game to play again.
                </Space>
                <Button
                  type="primary"
                  icon={<StopFilled />}
                  onClick={openResetModal}
                >
                  Reset
                </Button>
              </>
            ) : (
              <>
                <Space>
                  <HeartOutlined />
                  Thank you for playing The Strategists!
                </Space>
                <Button
                  type="primary"
                  icon={<LogoutOutlined />}
                  onClick={() => dispatch(UserActions.unsetUser())}
                >
                  Logout
                </Button>
              </>
            )}
          </Row>
        }
      >
        <PlayerStats player={player} />
        <VisualPortfolio perspective="player" playerLands={player.lands} />
      </Modal>
      {showResetModal ? (
        <ResetModal open={showResetModal} onCancel={closeResetModal} />
      ) : null}
    </>
  );
};
