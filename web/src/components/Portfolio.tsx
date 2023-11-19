import { useEffect } from 'react';
import { useSelector } from 'react-redux';
import { Land, Player, State } from '../redux';
import { Space, Table } from 'antd';
import {
  DollarCircleOutlined,
  HomeOutlined,
  PercentageOutlined,
} from '@ant-design/icons';
import { Chart, Dark, G2Spec } from '@antv/g2';
import { CssVariables } from '../App';

/**
 * -----  PLAYER PORTFOLIO BELOW  -----
 */

export interface PlayerPortfolioProps {
  player: Player;
  view: 'chart' | 'table';
}

export const PlayerPortfolio = (props: PlayerPortfolioProps) => {
  const { player, view } = props;
  return (
    <>
      {view === 'table' ? (
        <PlayerPortfolioTable player={player} />
      ) : (
        <PlayerPortfolioChart player={player} />
      )}
    </>
  );
};

/**
 * -----  PLAYER PORTFOLIO TABLE BELOW  -----
 */

export interface PlayerPortfolioTableProps {
  player: Player;
}

export const PlayerPortfolioTable = (props: PlayerPortfolioTableProps) => {
  const { lands } = useSelector((state: State) => state.lobby);
  const { player } = props;

  // Preparing map of lands for referencing lands' names
  const map = new Map<number, Land>();
  lands.forEach((land) => map.set(land.id, land));

  // Preparing datasource for the table
  const datasource = (player.lands || []).map((pl) => {
    return {
      ...pl,
      key: pl.landId,
      name: pl.landId ? map.get(pl.landId)?.name : 'Unknown',
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
  );
};

/**
 * -----  PLAYER PORTFOLIO CHART BELOW  -----
 */

export interface PlayerPortfolioChartProps {
  player: Player;
}

const prepareChartOptions = (): G2Spec => {
  return {
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
  };
};

export const PlayerPortfolioChart = (props: PlayerPortfolioChartProps) => {
  const { lands } = useSelector((state: State) => state.lobby);
  const { player } = props;

  // Preparing map of lands for referencing lands' names
  const map = new Map<number, Land>();
  lands.forEach((land) => map.set(land.id, land));

  // Preparing datasource for the visualization
  const datasource = (player.lands || []).map((land) => {
    const name = land.landId ? map.get(land.landId)?.name : '';
    return {
      name,
      ownership: land.ownership,
      buyAmount: land.buyAmount,
    };
  });

  useEffect(() => {
    // Creating chart's instance
    const chart = new Chart({
      container: 'portfolio-container',
      height: datasource.length * 80,
      paddingRight: 20, // Matches the max range of 'buyAmount'
    });

    // Configuring chart's theme
    chart.options(prepareChartOptions());

    // Setting up chart's data
    chart
      .point()
      .data(datasource)
      .encode('shape', 'point')
      .encode('y', 'name')
      .encode('x', 'ownership')
      .encode('size', 'buyAmount')
      .scale({
        x: { domain: [0, 100] },
        size: { range: [10, 20] },
      })
      .legend(false)
      .style('opacity', 0.75)
      .style('lineWidth', 1);

    // Rendering the chart
    chart.render();
  }, [datasource]);

  return <div id="portfolio-container"></div>;
};
