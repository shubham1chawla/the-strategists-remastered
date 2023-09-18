import { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Land, Player, PlayerLand, State, UserActions } from '../redux';
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
} from 'antd';
import {
  DollarCircleOutlined,
  ExclamationCircleOutlined,
  HomeOutlined,
  LogoutOutlined,
  PieChartOutlined,
  RiseOutlined,
  StockOutlined,
  StopFilled,
  StopOutlined,
  UserOutlined,
  WalletOutlined,
} from '@ant-design/icons';
import { Confetti, Stats } from '.';
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
            <>
              <ExclamationCircleOutlined />
              Investment capped at {maxOfferOwnership}% due to low cash!
            </>
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
  const { lands } = useSelector((state: State) => state.lobby);
  if (!open || !onCancel || !player) {
    return null;
  }

  // linking lands with the player
  const map = new Map<number, Land>();
  lands.forEach((land) => map.set(land.id, land));
  player.lands.forEach((pl) => {
    pl.land = pl.landId ? map.get(pl.landId) : pl.land;
  });

  return (
    <Modal
      className="strategists-map__modal"
      title={
        <div className="strategists-map__modal__title">
          <span>{`${player.username}'s Portfolio`}</span>
          <small>
            <StockOutlined /> {player.netWorth} current net worth
          </small>
        </div>
      }
      open={open}
      onCancel={onCancel}
      footer={null}
    >
      <Divider />
      <PlayerPortfolioTable lands={player.lands} />
    </Modal>
  );
};

export interface PlayerPortfolioTableProps {
  lands: PlayerLand[];
}

export const PlayerPortfolioTable = (props: PlayerPortfolioTableProps) => {
  const datasource = props.lands.map((pl) => {
    return {
      ...pl,
      key: pl.landId,
      name: pl.land?.name,
    };
  });

  return (
    <Table
      pagination={false}
      dataSource={datasource}
      columns={[
        {
          title: 'Name',
          dataIndex: 'name',
          key: 'name',
          render: (value) => (
            <>
              <HomeOutlined /> {value}
            </>
          ),
        },
        {
          title: 'Ownership',
          dataIndex: 'ownership',
          key: 'ownership',
          render: (value) => `${value}%`,
        },
        {
          title: 'Buy Amount',
          dataIndex: 'buyAmount',
          key: 'buyAmount',
          render: (value) => (
            <span>
              <DollarCircleOutlined /> {value}
            </span>
          ),
        },
      ]}
    />
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
  const { players } = useSelector((state: State) => state.lobby);
  if (!open || !onCancel || !land) {
    return null;
  }

  // linking players with the land
  const map = new Map<number, Player>();
  players.forEach((player) => map.set(player.id, player));
  land.players.forEach((pl) => {
    pl.player = pl.playerId ? map.get(pl.playerId) : pl.player;
  });

  return (
    <Modal
      className="strategists-map__modal"
      title={
        <div className="strategists-map__modal__title">
          <span>{`${land?.name}'s Investments`}</span>
          <small>
            <DollarCircleOutlined /> {land?.marketValue} current market value
          </small>
        </div>
      }
      open={open}
      onCancel={onCancel}
      footer={null}
    >
      <Divider />
      <LandInvestmentTable players={land.players} />
    </Modal>
  );
};

export interface LandInvestmentTableProps {
  players: PlayerLand[];
}

export const LandInvestmentTable = (props: LandInvestmentTableProps) => {
  const datasource = props.players
    .filter((pl) => pl.player?.state !== 'BANKRUPT')
    .map((pl) => {
      return {
        ...pl,
        key: pl.playerId,
        name: pl.player?.username,
      };
    });

  return (
    <Table
      pagination={false}
      dataSource={datasource}
      columns={[
        {
          title: 'Name',
          dataIndex: 'name',
          key: 'name',
          render: (value) => (
            <>
              <UserOutlined /> {value}
            </>
          ),
        },
        {
          title: 'Ownership',
          dataIndex: 'ownership',
          key: 'ownership',
          render: (value) => `${value}%`,
        },
        {
          title: 'Buy Amount',
          dataIndex: 'buyAmount',
          key: 'buyAmount',
          render: (value) => (
            <span>
              <DollarCircleOutlined /> {value}
            </span>
          ),
        },
      ]}
    />
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

export interface WinModalProps extends BaseModalProps {
  // No additional fields
}

export const WinModal = (props: WinModalProps) => {
  const { user, lobby } = useSelector((state: State) => state);
  const [showResetModal, setShowResetModal] = useState(false);
  const { open, onCancel } = props;
  const dispatch = useDispatch();

  // determining the winner
  const player = lobby.players.find((p) => p.state === 'ACTIVE');

  return (
    <>
      <Confetti type="multiple" />
      <Modal
        open={open}
        onCancel={onCancel}
        closable={false}
        maskClosable={false}
        title="The Strategists Winner"
        footer={
          user.type === 'admin' ? (
            <>
              <Button
                type="primary"
                icon={<StopFilled />}
                onClick={() => setShowResetModal(true)}
              >
                Reset
              </Button>
            </>
          ) : (
            <Button
              type="primary"
              icon={<LogoutOutlined />}
              onClick={() => dispatch(UserActions.unsetUser())}
            >
              Logout
            </Button>
          )
        }
      >
        <Stats player={player} />
      </Modal>
      {showResetModal ? (
        <ResetModal
          open={showResetModal}
          onCancel={() => setShowResetModal(false)}
        />
      ) : null}
    </>
  );
};
