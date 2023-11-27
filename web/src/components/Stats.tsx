import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import {
  Card,
  Col,
  Divider,
  Row,
  Select,
  Space,
  Statistic,
  Table,
  Tag,
} from 'antd';
import { ColumnType } from 'antd/es/table';
import { Chart, Dark, TooltipItem } from '@antv/g2';
import {
  AuditOutlined,
  DollarCircleOutlined,
  DollarOutlined,
  HomeOutlined,
  InfoCircleOutlined,
  PercentageOutlined,
  PieChartOutlined,
  StockOutlined,
  UserOutlined,
  WalletOutlined,
} from '@ant-design/icons';
import { Land, Player, PlayerLand, State } from '../redux';
import { CssVariables } from '../App';
import axios from 'axios';

/**
 * -----  PLAYER STATS BELOW  -----
 */

export interface PlayerStatsProps {
  player: Player;
}

export const PlayerStats = (props: PlayerStatsProps) => {
  const { player } = props;
  return (
    <div className="strategists-stats">
      <Row>
        <Col span={24}>
          <Divider>
            <Space>
              <Tag icon={<UserOutlined />}>{player?.username}</Tag>
              {player.state === 'BANKRUPT' ? (
                <Tag icon={<AuditOutlined />}>Bankrupt</Tag>
              ) : null}
            </Space>
          </Divider>
        </Col>
      </Row>
      <Row>
        <Col span={12}>
          <Card bordered={false}>
            <Statistic
              title={
                <Space>
                  <WalletOutlined />
                  Cash
                </Space>
              }
              value={player?.cash}
              precision={2}
              prefix={<DollarOutlined />}
            />
          </Card>
        </Col>
        <Col span={12}>
          <Card bordered={false}>
            <Statistic
              title={
                <Space>
                  <StockOutlined />
                  Net Worth
                </Space>
              }
              value={player?.netWorth}
              precision={2}
              prefix={<DollarOutlined />}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

/**
 * -----  LAND STATS BELOW  -----
 */

export interface LandStatsProps {
  land: Land;
}

export const LandStats = (props: LandStatsProps) => {
  const { land } = props;
  return (
    <div className="strategists-stats">
      <Row>
        <Col span={24}>
          <Divider>
            <Tag icon={<HomeOutlined />}>{land?.name}</Tag>
          </Divider>
        </Col>
      </Row>
      <Row>
        <Col span={12}>
          <Card bordered={false}>
            <Statistic
              title={
                <Space>
                  <StockOutlined />
                  Market Value
                </Space>
              }
              value={land?.marketValue}
              precision={2}
              prefix={<DollarOutlined />}
            />
          </Card>
        </Col>
        <Col span={12}>
          <Card bordered={false}>
            <Statistic
              title={
                <Space>
                  <PieChartOutlined />
                  Total Ownership
                </Space>
              }
              value={land?.totalOwnership}
              suffix={<PercentageOutlined />}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

/**
 * -----  PORTFOLIO BELOW  -----
 */

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

    // Configuring chart's options
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
      theme: getChartTheme(),
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
        items: getPortfolioTooltipItems(props),
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
 * -----  TRENDS BELOW  -----
 */

export interface TrendsProps {
  perspective: 'player' | 'land';
  id: number;
}

export const Trends = (props: TrendsProps) => {
  const { perspective, id } = props;

  useEffect(() => {
    // Creating chart's instance
    const chart = new Chart({
      container: 'trends-container',
      height: 300,
    });

    // Configuring chart's options
    chart.options({
      autoFit: true,
      axis: {
        x: {
          grid: false,
          title: false,
          tickFilter: (value: number) => Number.isInteger(value),
        },
        y: {
          grid: true,
          title: false,
          tickCount: 3,
          labelFormatter: (value: number) => `$${value}`,
        },
      },
      theme: getChartTheme(),
    });

    // Updating tooltip position
    chart.interaction('tooltip', {
      position: 'left', // Matching the positioning of map's tooltip
    });

    // Calling API to fetch trends data
    axios.get(`/api/${perspective}s/${id}/trends`).then((response) => {
      perspective === 'player'
        ? drawPlayerTrends(chart, response.data)
        : drawLandTrends(chart, response.data);
      chart.render();
    });
  }, [perspective, id]);

  return <div id="trends-container"></div>;
};

interface PlayerTrend {
  playerId: number;
  cash: number;
  netWorth: number;
}

const drawPlayerTrends = (chart: Chart, trends: PlayerTrend[]) => {
  // Creating player's net worth trends
  const netWorths = trends.map(({ netWorth }, i) => ({
    turn: i + 1,
    netWorth,
  }));

  // Adding area marks
  chart
    .area()
    .data(netWorths)
    .encode('x', 'turn')
    .encode('y', 'netWorth')
    .scale('y', { domainMin: 0 })
    .style(
      'fill',
      `linear-gradient(-90deg, rgba(0, 0, 0, 0) 0%, ${CssVariables['--accent-color']} 100%)`
    )
    .tooltip({
      title: '',
      items: [
        {
          name: 'Turn',
          field: 'turn',
          color: 'transparent',
        },
      ],
    });

  // Adding line marks
  chart
    .line()
    .data(netWorths)
    .encode('x', 'turn')
    .encode('y', 'netWorth')
    .scale('y', { domainMin: 0 })
    .style('stroke', CssVariables['--accent-color'])
    .tooltip({
      title: '',
      items: [
        {
          name: 'Net Worth',
          field: 'netWorth',
          color: 'transparent',
          valueFormatter: (value: number) => `$${value}`,
        },
      ],
    });

  // Creating player's cash trends
  const cashs = trends.map(({ cash }, i) => ({
    turn: i + 1,
    cash,
  }));

  // Adding cash line marks
  chart
    .line()
    .data(cashs)
    .encode('x', 'turn')
    .encode('y', 'cash')
    .scale('y', { domainMin: 0 })
    .style('stroke', CssVariables['--text-color'])
    .tooltip({
      title: '',
      items: [
        {
          name: 'Cash',
          field: 'cash',
          color: 'transparent',
          valueFormatter: (value: number) => `$${value}`,
        },
      ],
    });
};

interface LandTrend {
  landId: number;
  marketValue: number;
}

const drawLandTrends = (chart: Chart, trends: LandTrend[]) => {
  // Creating player's net worth trends
  const marketValues = trends.map(({ marketValue }, i) => ({
    turn: i + 1,
    marketValue,
  }));

  // Adding area marks
  chart
    .area()
    .data(marketValues)
    .encode('x', 'turn')
    .encode('y', 'marketValue')
    .scale('y', { domainMin: 0 })
    .style(
      'fill',
      `linear-gradient(-90deg, rgba(0, 0, 0, 0) 0%, ${CssVariables['--accent-color']} 100%)`
    )
    .tooltip({
      title: '',
      items: [
        {
          name: 'Turn',
          field: 'turn',
          color: 'transparent',
        },
      ],
    });

  // Adding line marks
  chart
    .line()
    .data(marketValues)
    .encode('x', 'turn')
    .encode('y', 'marketValue')
    .scale('y', { domainMin: 0 })
    .style('stroke', CssVariables['--accent-color'])
    .tooltip({
      title: '',
      items: [
        {
          name: 'Market Value',
          field: 'marketValue',
          color: 'transparent',
          valueFormatter: (value: number) => `$${value}`,
        },
      ],
    });
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

const getPortfolioTooltipItems = (props: PortfolioProps): TooltipItem[] => {
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

export const getChartTheme = () => {
  return {
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
  };
};
