import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Land, Player, State, UserActions } from '../redux';
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
  StockOutlined,
  StopFilled,
  StopOutlined,
  UserOutlined,
  WalletOutlined,
} from '@ant-design/icons';
import { Confetti, Logo, Stats } from '.';
import axios from 'axios';

interface BaseModalProps {
  open: boolean;
  onCancel: () => void;
}

/**
 * -----  INVEST MODAL BELOW  -----
 */

export interface PlayerInvestModalProps extends BaseModalProps {
  player: Player;
  land: Land;
  investText: string;
}

export const PlayerInvestModal = (props: Partial<PlayerInvestModalProps>) => {
  const { open, player, land, investText, onCancel } = props;
  const [ownership, setOwnership] = useState(0);
  if (!open || !player || !land || !investText || !onCancel) {
    return null;
  }

  // calculating investment-related parameters
  const userInvestAmount = (ownership * land.marketValue) / 100;
  const maxAvailOwnership = 100 - land.totalOwnership;
  const maxOfferOwnership = Math.min(
    maxAvailOwnership,
    Math.floor((player.cash * 100) / land.marketValue)
  );

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
      title={
        <div className="strategists-actions__modal__title">
          {investText}
          <Space>
            <small>
              <WalletOutlined /> {player.cash} cash available
            </small>
            <Divider type="vertical" />
            <small>
              <PieChartOutlined /> {maxAvailOwnership}% shares available
            </small>
          </Space>
        </div>
      }
      open={!!open}
      okText={
        <>
          <RiseOutlined /> Invest
        </>
      }
      onOk={invest}
      onCancel={onCancel}
      okButtonProps={{
        disabled: userInvestAmount > player.cash,
      }}
    >
      <Divider />
      <main className="strategists-actions__modal__body">
        <Row>
          <Col span={12}>
            <Card bordered={false}>
              <Statistic
                title="Proposed Ownership"
                value={ownership}
                precision={0}
                prefix={<RiseOutlined />}
                suffix="%"
              />
            </Card>
          </Col>
          <Col span={12}>
            <Card bordered={false}>
              <Statistic
                title="Cost of Investment"
                value={userInvestAmount}
                precision={2}
                prefix={<DollarCircleOutlined />}
              />
            </Card>
          </Col>
        </Row>
        <Slider
          defaultValue={ownership}
          min={0}
          max={maxOfferOwnership}
          onAfterChange={(value) => setOwnership(value)}
          tooltip={{
            formatter: (value) => `${value}%`,
          }}
        />
        {
          // This will show a warning to user that their current balance is less then the maximum available ownership.
          maxOfferOwnership !== maxAvailOwnership ? (
            <Row justify="center">
              <Space>
                <ExclamationCircleOutlined />
                <span>
                  Investment capped at {maxOfferOwnership}% due to low cash!
                </span>
              </Space>
            </Row>
          ) : null
        }
      </main>
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
      className="strategists-map__modal"
      title={
        <div className="strategists-map__modal__title">
          <span>
            <UserOutlined /> {player.username}
          </span>
          <small>
            <StockOutlined /> {player.netWorth} current net worth
          </small>
        </div>
      }
      open={open}
      onCancel={onCancel}
      footer={null}
    >
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

  const datasource = player.lands.map((pl) => {
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
      className="strategists-map__modal"
      title={
        <div className="strategists-map__modal__title">
          <Space>
            <HomeOutlined />
            {land.name}
          </Space>
          <small>
            <DollarCircleOutlined /> {land?.marketValue} current market value
          </small>
        </div>
      }
      open={open}
      onCancel={onCancel}
      footer={null}
    >
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

  const datasource = land.players
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
            {type === 'admin' ? (
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
        <Stats player={player} />
        <PlayerPortfolioTable player={player} />
      </Modal>
      {showResetModal ? (
        <ResetModal open={showResetModal} onCancel={closeResetModal} />
      ) : null}
    </>
  );
};
