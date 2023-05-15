import { HomeOutlined, UserOutlined, WalletOutlined } from '@ant-design/icons';
import { Divider, Modal, Table } from 'antd';
import { Land, Player, PlayerLand, State } from '../redux';
import { ColumnsType } from 'antd/es/table';
import { useSelector } from 'react-redux';

export interface MapModalProps {
  type?: 'player' | 'land';
  id?: number;
  onCancel?: () => void;
}

export const MapModal = (props: MapModalProps) => {
  const { type, id, onCancel } = props;
  const lobby = useSelector((state: State) => state.lobby);
  if (!type || !id || !onCancel) {
    return null;
  }

  // preparing maps for faster access
  const players = new Map<number, Player>();
  const lands = new Map<number, Land>();
  lobby.players.forEach((player) => players.set(player.id, player));
  lobby.lands.forEach((land) => lands.set(land.id, land));

  // preparing node links
  const player: Player | undefined =
    type === 'player' ? players.get(id) : undefined;
  const land: Land | undefined = type === 'land' ? lands.get(id) : undefined;
  (player?.lands || land?.players || []).forEach((pl) => {
    pl.land = pl.landId ? lands.get(pl.landId) : pl.land;
    pl.player = pl.playerId ? players.get(pl.playerId) : pl.player;
  });

  return (
    <Modal
      className="strategists-map__modal"
      title={renderTitle(player, land)}
      open={!!type}
      onCancel={onCancel}
      footer={[]}
    >
      <Divider />
      {renderTable(player, land)}
    </Modal>
  );
};

const renderTitle = (player?: Player, land?: Land) => {
  return (
    <div className="strategists-map__modal__title">
      <span>
        {player?.username || land?.name}'s{' '}
        {player ? 'investments' : 'distribution'}
      </span>
      <small>
        <WalletOutlined /> {player?.netWorth || land?.marketValue}
      </small>
    </div>
  );
};

const renderTable = (player?: Player, land?: Land) => {
  // preparing dynamic column definitions
  const columns: ColumnsType<any> = [
    {
      title: 'Name',
      dataIndex: 'name',
      key: 'name',
      render: (value) => (
        <span>
          {land ? <UserOutlined /> : <HomeOutlined />} {value}
        </span>
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
          <WalletOutlined /> {value}
        </span>
      ),
    },
  ];

  // preparing expected datasource instance
  const dataSource = (player?.lands || land?.players || []).map(
    (pl: PlayerLand) => {
      return {
        key: (pl.player || pl.land)?.id,
        name: pl.player?.username || pl.land?.name,
        ownership: pl.ownership,
        buyAmount: pl.buyAmount,
      };
    }
  );

  return <Table dataSource={dataSource} columns={columns}></Table>;
};
