import { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
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
  Tabs,
  Tag,
  notification,
} from 'antd';
import { SliderMarks } from 'antd/es/slider';
import {
  CheckCircleOutlined,
  CrownOutlined,
  DollarCircleOutlined,
  ExclamationCircleOutlined,
  HeartOutlined,
  HomeOutlined,
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
  TabularPortfolio,
  VisualPortfolio,
  VisualPrediction,
  VisualTrend,
} from '.';
import { Land, LoginActions, Player, useLobby, useLogin } from '../redux';
import { InvestmentStrategy } from '../utils';
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
  gameCode: string;
  player: Player;
  land: Land;
  title: string;
}

export const PlayerInvestModal = (props: Partial<PlayerInvestModalProps>) => {
  const { open, gameCode, player, land, title, onCancel } = props;
  const [ownership, setOwnership] = useState(0);
  const [investing, setInvesting] = useState(false);
  const [api, contextHolder] = notification.useNotification();

  // Validating props
  if (!open || !gameCode || !player || !land || !title || !onCancel) {
    return null;
  }

  // Closing the modal if player doesn't have the turn
  if (!player.turn) {
    onCancel();
    return null;
  }

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
      api.error({
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
    onCancel();
  };

  return (
    <>
      {contextHolder}
      <Modal
        className="strategists-actions__modal"
        title="Investment Strategy"
        open={!!open}
        onCancel={onModalCancel}
        footer={
          <Row justify="space-between" wrap={false}>
            <Space>
              {strategy.maxOfferableOwnership !==
              strategy.availableOwnership ? (
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
                <TabularPortfolio
                  perspective="land"
                  playerLands={land.players}
                />
              ),
            },
          ]}
        />
      </Modal>
    </>
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
  const id = land ? land.id : player ? player.id : -1;

  const tabItems = [
    {
      key: '1',
      label: 'Trends',
      children: (
        <VisualTrend perspective={perspective} id={id} showHelp={true} />
      ),
    },
    {
      key: '2',
      label: 'Portfolio',
      children: (
        <VisualPortfolio
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
      children: <VisualPrediction player={player} showHelp={true} />,
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

/**
 * -----  RESET MODAL BELOW  -----
 */

export interface ResetModalProps extends BaseModalProps {
  gameCode: string;
}

export const ResetModal = (props: ResetModalProps) => {
  const [checked, setChecked] = useState(false);
  const { open, onCancel, gameCode } = props;

  const reset = async () => {
    await axios.delete(`/api/games/${gameCode}`);
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
  const { gameCode, player } = useLogin();
  const { winnerPlayer } = useLobby();
  const [showResetModal, setShowResetModal] = useState(false);
  const dispatch = useDispatch();

  if (!gameCode || !winnerPlayer) return null;
  return (
    <>
      <Confetti type="multiple" />
      <Modal
        open={!!winnerPlayer}
        onCancel={() => setShowResetModal(false)}
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
            {player?.host ? (
              <>
                <Space>
                  <InfoCircleOutlined />
                  Reset the game to play again.
                </Space>
                <Button
                  type="primary"
                  icon={<StopFilled />}
                  onClick={() => setShowResetModal(true)}
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
                  onClick={() => dispatch(LoginActions.logout())}
                >
                  Logout
                </Button>
              </>
            )}
          </Row>
        }
      >
        <PlayerStats player={winnerPlayer} />
        <Tabs
          centered
          defaultActiveKey="1"
          size="large"
          items={[
            {
              key: '1',
              label: 'Trends',
              children: (
                <VisualTrend perspective="player" id={winnerPlayer.id} />
              ),
            },
            {
              key: '2',
              label: 'Portfolio',
              children: (
                <VisualPortfolio
                  perspective="player"
                  playerLands={winnerPlayer.lands}
                />
              ),
            },
            {
              key: '3',
              label: 'Predictions',
              children: <VisualPrediction player={winnerPlayer} />,
            },
          ]}
        />
      </Modal>
      {showResetModal && (
        <ResetModal
          open={showResetModal}
          gameCode={gameCode}
          onCancel={() => setShowResetModal(false)}
        />
      )}
    </>
  );
};

/**
 * -----  TURN MODAL BELOW  -----
 */

export const TurnModal = () => {
  const { player } = useLogin();
  const { lands } = useLobby();
  const [open, setOpen] = useState(false);

  useEffect(() => {
    setOpen(player?.turn || false);
  }, [player]);

  if (!player) {
    return null;
  }
  return (
    <Modal
      open={!!player && open}
      footer={null}
      onCancel={() => setOpen(false)}
      width={400}
      closeIcon={false}
      centered
    >
      <Alert
        type="info"
        message="It's your turn!"
        description={
          <Space>
            You have landed on
            <Tag icon={<HomeOutlined />}>{lands[player.index].name}</Tag>
          </Space>
        }
        showIcon
        banner
      />
    </Modal>
  );
};
