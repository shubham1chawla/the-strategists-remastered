import { Space, Table } from 'antd';
import { DollarCircleOutlined, PercentageOutlined } from '@ant-design/icons';
import usePortfolioItems from '@game/hooks/usePortfolioItems';
import { PlayerLand } from '@game/state';
import LandAvatar from './LandAvatar';
import PlayerAvatar from './PlayerAvatar';

export interface PortfolioTableProps {
  perspective: 'player' | 'land';
  playerLands: PlayerLand[];
}

function PortfolioTable(props: PortfolioTableProps) {
  const { perspective, playerLands } = props;
  const portfolioItems = usePortfolioItems(perspective, playerLands);
  return (
    <Table
      pagination={false}
      dataSource={portfolioItems}
      columns={[
        {
          title: 'Name',
          dataIndex: 'name',
          key: 'name',
          render: (value) => (
            <Space>
              {perspective === 'player' ? (
                <LandAvatar name={value} />
              ) : (
                <PlayerAvatar username={value} />
              )}
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
  );
}

export default PortfolioTable;
