import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { Land, Player, PlayerLand, State } from '../redux';
import { Row, Select, Space, Table } from 'antd';
import { ColumnType } from 'antd/es/table';
import {
  DollarCircleOutlined,
  HomeOutlined,
  InfoCircleOutlined,
  PercentageOutlined,
  UserOutlined,
} from '@ant-design/icons';
import { Chart, Dark, TooltipItem } from '@antv/g2';
import { CssVariables } from '../App';

export interface PortfolioProps {
  perspective: 'player' | 'land';
  playerLands: PlayerLand[];
}

export const Portfolio = (props: PortfolioProps) => {
  const [view, setView] = useState<'Visual' | 'Tabular'>('Visual');
  return (
    <>
      {view === 'Visual' ? (
        <VisualPortfolio {...props} />
      ) : (
        <TabularPortfolio {...props} />
      )}
      <br />
      <Row align="middle" justify="space-between">
        <Space>
          <InfoCircleOutlined /> Change portfolio view
        </Space>
        <Select
          value={view}
          onChange={setView}
          options={['Visual', 'Tabular'].map((value) => ({ value }))}
        />
      </Row>
    </>
  );
};

export const TabularPortfolio = (props: PortfolioProps) => {
  const { players, lands } = useSelector((state: State) => state.lobby);
  return (
    <Table
      pagination={false}
      dataSource={getPortfolioItems(props, players, lands)}
      columns={getColumnTypes(props)}
    />
  );
};

export const VisualPortfolio = (props: PortfolioProps) => {
  const { players, lands } = useSelector((state: State) => state.lobby);

  useEffect(() => {
    const items = getPortfolioItems(props, players, lands);

    // Creating chart's instance
    const chart = new Chart({
      container: 'portfolio-container',
      height: items.length * 80 || 100,
      paddingRight: 20, // Matches the max range of 'buyAmount'
    });

    // Configuring chart's theme
    chart.options({
      autoFit: true,
      axis: {
        x: {
          title: false,
          tickCount: 3,
          labelFormatter: (value: number) => `${value}%`,
        },
        y: {
          title: false,
        },
      },
      theme: {
        ...Dark(),
        axisLeft: {
          labelFill: CssVariables['--text-color'],
          labelOpacity: 1,
        },
        axisBottom: {
          labelFill: CssVariables['--text-color'],
          labelOpacity: 1,
        },
        color: CssVariables['--accent-color'],
        view: {
          viewFill: 'transparent',
        },
      },
    });

    // Setting up chart's data
    chart
      .point()
      .data(items)
      .encode('shape', 'point')
      .encode('y', 'name')
      .encode('x', 'ownership')
      .encode('size', 'buyAmount')
      .scale({
        x: { domain: [0, 100] },
        size: { range: [5, 20] },
      })
      .legend(false)
      .style('opacity', 0.75)
      .style('lineWidth', 1)
      .tooltip({
        items: getTooltipItems(props),
      });

    // Updating tooltip position
    chart.interaction('tooltip', {
      position: 'left', // Matching the positioning of map's tooltip
    });

    // Rendering the chart
    chart.render();
  }, [players, lands, props]);

  return <div id="portfolio-container"></div>;
};

/**
 * -----  UTILITIES DEFINED BELOW  -----
 */

interface PortfolioItem extends PlayerLand {
  name: string;
  key?: number;
}

const getPortfolioItems = (
  props: PortfolioProps,
  players: Player[],
  lands: Land[]
): PortfolioItem[] => {
  const { perspective, playerLands } = props;

  // Preparing maps for better random access
  const playerMap = new Map<number, Player>();
  players.forEach((player) => playerMap.set(player.id, player));
  const landMap = new Map<number, Land>();
  lands.forEach((land) => landMap.set(land.id, land));

  // Enriching player lands information as per perspective
  return (playerLands || [])
    .filter((pl) => {
      if (perspective === 'player') return true;
      return !pl.playerId || playerMap.get(pl.playerId)?.state !== 'BANKRUPT';
    })
    .map((pl) => {
      const key = perspective === 'player' ? pl.landId : pl.playerId;
      const name = key
        ? perspective === 'player'
          ? landMap.get(key)?.name
          : playerMap.get(key)?.username
        : undefined;
      return {
        ...pl,
        key,
        name: name || 'Unknown',
      };
    });
};

const getColumnTypes = (props: PortfolioProps): ColumnType<PortfolioItem>[] => {
  const { perspective } = props;
  return [
    {
      title: 'Name',
      dataIndex: 'name',
      key: 'name',
      render: (value) => (
        <Space>
          {perspective === 'player' ? <HomeOutlined /> : <UserOutlined />}
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
  ];
};

const getTooltipItems = (props: PortfolioProps): TooltipItem[] => {
  const { perspective } = props;
  return [
    {
      name: perspective === 'player' ? 'Property' : 'Player',
      field: 'name',
      color: 'transparent',
    },
    {
      name: 'Ownership',
      field: 'ownership',
      color: 'transparent',
      valueFormatter: (value: number) => `${value}%`,
    },
    {
      name: 'Investment Amount',
      field: 'buyAmount',
      color: 'transparent',
      valueFormatter: (value: number) => `$${value}`,
    },
  ];
};
