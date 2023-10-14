import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Land, Player, State, UserActions } from '../redux';
import { InvestmentStrategy } from '../utils';
import {
  Alert,
  Button,
  Card,
  Checkbox,
  Col,
  Divider,
  Modal,
  Row,
  Slider,
  Space,
  Statistic,
  Table,
  Tag,
} from 'antd';
import {
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
  UserOutlined,
  WalletOutlined,
} from '@ant-design/icons';
import { Confetti, LandStats, Logo, PlayerStats } from '.';
import axios from 'axios';

export interface BaseModalProps {
  open: boolean;
  onCancel: () => void;
}

/**
 * -----  INVEST MODAL BELOW  -----
 */

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

  // setting up investment strategy
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
      <Slider
        defaultValue={ownership}
        min={0}
        max={strategy.maxOfferableOwnership}
        onAfterChange={(value) => setOwnership(value)}
        tooltip={{
          formatter: (value) => `${value}%`,
        }}
        autoFocus={true}
      />
      <Row justify="center">
        <Space>
          <InfoCircleOutlined />
          Use this slider to adjust your investment offer.
        </Space>
      </Row>
      <Divider />
    </Modal>
  );
};

/**
 * -----  PLAYER PORTFOLIO MODAL BELOW  -----
 */

export interface PlayerPortfolioModalProps extends BaseModalProps {
  player: Player;
}

export const PlayerPortfolioModal = (
  props: Partial<PlayerPortfolioModalProps>
) => {
  const { open, onCancel, player } = props;
  if (!open || !onCancel || !player) {
    return null;
  }

  return (
    <Modal
      title="Portfolio Analysis"
      open={open}
      onCancel={onCancel}
      footer={null}
    >
      <PlayerStats player={player} />
      <PlayerPortfolioTable player={player} />
    </Modal>
  );
};

export interface PlayerPortfolioTableProps {
  player: Player;
}

export const PlayerPortfolioTable = (props: PlayerPortfolioTableProps) => {
  const { lands } = useSelector((state: State) => state.lobby);
  const { player } = props;

  // preparing map of lands for referencing lands' names
  const map = new Map<number, Land>();
  lands.forEach((land) => map.set(land.id, land));

  const datasource = (player.lands || []).map((pl) => {
    return {
      ...pl,
      key: pl.landId,
      name: pl.landId ? map.get(pl.landId)?.name : 'Unknown',
    };
  });

  return (
    <>
      <Divider>
        <SlidersOutlined /> Portfolio
      </Divider>
      <Table
        pagination={false}
        dataSource={datasource}
        columns={[
          {
            title: 'Name',
            dataIndex: 'name',
            key: 'name',
            render: (value) => (
              <Space>
                <HomeOutlined />
                {value}
              </Space>
            ),
          },
          {
            title: 'Ownership',
            dataIndex: 'ownership',
            key: 'ownership',
            render: (value) => (
              <Space>
                {value}
                <PercentageOutlined />
              </Space>
            ),
          },
          {
            title: 'Buy Amount',
            dataIndex: 'buyAmount',
            key: 'buyAmount',
            render: (value) => (
              <Space>
                <DollarCircleOutlined />
                {value}
              </Space>
            ),
          },
        ]}
      />
    </>
  );
};

/**
 * -----  LAND INVESTMENT MODAL BELOW  -----
 */

export interface LandInvestmentModalProps extends BaseModalProps {
  land: Land;
}

export const LandInvestmentModal = (
  props: Partial<LandInvestmentModalProps>
) => {
  const { open, onCancel, land } = props;
  if (!open || !onCancel || !land) {
    return null;
  }

  return (
    <Modal
      title="Investments' Analysis"
      open={open}
      onCancel={onCancel}
      footer={null}
    >
      <LandStats land={land} />
      <LandInvestmentTable land={land} />
    </Modal>
  );
};

export interface LandInvestmentTableProps {
  land: Land;
}

export const LandInvestmentTable = (props: LandInvestmentTableProps) => {
  const { players } = useSelector((state: State) => state.lobby);
  const { land } = props;

  // preparing map of players for referencing players' usernames
  const map = new Map<number, Player>();
  players.forEach((player) => map.set(player.id, player));

  const datasource = (land.players || [])
    .filter((pl) => !pl.playerId || map.get(pl.playerId)?.state !== 'BANKRUPT')
    .map((pl) => {
      return {
        ...pl,
        key: pl.playerId,
        name: pl.playerId ? map.get(pl.playerId)?.username : 'Unknown',
      };
    });

  return (
    <>
      <Divider>
        <SlidersOutlined /> Investments
      </Divider>
      <Table
        pagination={false}
        dataSource={datasource}
        columns={[
          {
            title: 'Name',
            dataIndex: 'name',
            key: 'name',
            render: (value) => (
              <Space>
                <UserOutlined />
                {value}
              </Space>
            ),
          },
          {
            title: 'Ownership',
            dataIndex: 'ownership',
            key: 'ownership',
            render: (value) => (
              <Space>
                {value}
                <PercentageOutlined />
              </Space>
            ),
          },
          {
            title: 'Buy Amount',
            dataIndex: 'buyAmount',
            key: 'buyAmount',
            render: (value) => (
              <Space>
                <DollarCircleOutlined />
                {value}
              </Space>
            ),
          },
        ]}
      />
    </>
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
  const [player, setPlayer] = useState<Player | null>(null);
  const { user, lobby } = useSelector((state: State) => state);
  const { type } = user;
  const { players, state } = lobby;

  const [showResetModal, setShowResetModal] = useState(false);
  const dispatch = useDispatch();

  const closeResetModal = () => setShowResetModal(false);
  const openResetModal = () => setShowResetModal(true);

  useEffect(() => {
    // determining whether to show win modal
    const activePlayers = players.filter((player) => player.state === 'ACTIVE');
    setPlayer(
      state === 'ACTIVE' && activePlayers.length === 1 ? activePlayers[0] : null
    );
  }, [players, state]);

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
        <PlayerPortfolioTable player={player} />
      </Modal>
      {showResetModal ? (
        <ResetModal open={showResetModal} onCancel={closeResetModal} />
      ) : null}
    </>
  );
};
