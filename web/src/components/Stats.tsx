import { useEffect, useState } from 'react';
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
  HeartFilled,
  HeartOutlined,
  HomeOutlined,
  InfoCircleOutlined,
  PercentageOutlined,
  PieChartOutlined,
  StockOutlined,
  UserOutlined,
  WalletOutlined,
} from '@ant-design/icons';
import {
  Land,
  LandTrend,
  Player,
  PlayerLand,
  PlayerTrend,
  Prediction,
  useLobby,
  usePredictions,
  useTrends,
} from '../redux';
import { CssVariables } from '../App';

/**
 * -----  PLAYER STATS BELOW  -----
 */

export interface PlayerStatsProps {
  player: Player;
  showRemainingSkipsCount?: boolean;
}

export const PlayerStats = (props: PlayerStatsProps) => {
  const { player } = props;

  // Determining whether to show remaining skip counts
  const showRemainingSkipsCount =
    !!player.remainingSkipsCount &&
    !!player.allowedSkipsCount &&
    !!props.showRemainingSkipsCount &&
    player.state !== 'BANKRUPT';

  const remainingSkipsCount = player.remainingSkipsCount || 0;
  const allowedSkipsCount = player.allowedSkipsCount || 0;
  const skipsCount = allowedSkipsCount - remainingSkipsCount;

  return (
    <div className="strategists-stats">
      <Row>
        <Col span={24}>
          <Divider>
            <Space>
              <Tag icon={<UserOutlined />}>{player?.username}</Tag>
              {player.state === 'BANKRUPT' && (
                <Tag icon={<AuditOutlined />}>Bankrupt</Tag>
              )}
              {showRemainingSkipsCount && (
                <Tag>
                  {[...Array(remainingSkipsCount)].map((_, i) => (
                    <HeartFilled key={i} />
                  ))}
                  {[...Array(skipsCount)].map((_, i) => (
                    <HeartOutlined key={i} />
                  ))}
                </Tag>
              )}
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
  const { players, lands } = useLobby();
  return (
    <Table
      pagination={false}
      dataSource={getPortfolioItems(props, players, lands)}
      columns={getColumnTypes(props)}
    />
  );
};

export const VisualPortfolio = (props: PortfolioProps) => {
  const { players, lands } = useLobby();

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
  const { playerTrends, landTrends } = useTrends();
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

    switch (perspective) {
      case 'player':
        drawPlayerTrends(
          chart,
          playerTrends.filter(({ playerId }) => playerId === id)
        );
        break;
      case 'land':
        drawLandTrends(
          chart,
          landTrends.filter(({ landId }) => landId === id)
        );
        break;
    }
    chart.render();
  }, [id, perspective, playerTrends, landTrends]);

  return <div id="trends-container"></div>;
};

const drawPlayerTrends = (chart: Chart, trends: PlayerTrend[]) => {
  // Adding area marks
  chart
    .area()
    .data(trends)
    .encode('x', 'turn')
    .encode('y', 'netWorth')
    .scale('y', { domainMin: 0 })
    .style(
      'fill',
      `linear-gradient(-90deg, rgba(0, 0, 0, 0) 0%, ${CssVariables['--accent-color']} 100%)`
    )
    .tooltip(false);

  // Adding line marks
  chart
    .line()
    .data(trends)
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

  // Adding cash line marks
  chart
    .line()
    .data(trends)
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

const drawLandTrends = (chart: Chart, trends: LandTrend[]) => {
  // Adding area marks
  chart
    .area()
    .data(trends)
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
    .data(trends)
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
 * -----  PREDICTION BELOW  -----
 */

export interface VisualPredictionProps {
  player: Player;
}

export const VisualPrediction = (props: VisualPredictionProps) => {
  const predictions = usePredictions();
  const { playerTrends } = useTrends();
  const { players } = useLobby();
  const { player } = props;

  useEffect(() => {
    // Creating chart's instance
    const chart = new Chart({
      container: 'predictions-container',
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
          tickFilter: (value: number) => false,
        },
      },
      scale: {
        color: {
          range: [CssVariables['--accent-color'], CssVariables['--text-color']],
        },
      },
      legend: {
        color: {
          position: 'bottom',
          layout: {
            justifyContent: 'center',
            alignItems: 'center',
            flexDirection: 'column',
          },
          labelFormatter: (label: string) =>
            label === 'Others'
              ? "Others' winning probability"
              : `${label}'s winning probability`,
        },
      },
      theme: getChartTheme(),
    });

    // Drawing predictions visualization
    const items = getVisualPredictionItems(
      player,
      players,
      predictions,
      playerTrends
    );
    drawPredictions(chart, player, items);

    // Rendering chart
    chart.render();
  }, [player, players, predictions, playerTrends]);

  return <div id="predictions-container"></div>;
};

const drawPredictions = (
  chart: Chart,
  player: Player,
  items: VisualPredictionItem[]
) => {
  // Adding data to the chart's instance
  chart
    .data(items)
    .transform([{ type: 'stackY' }, { type: 'normalizeY' }])
    .encode('x', 'turn')
    .encode('y', 'share')
    .encode('color', 'name');

  // Styling & tooltip interactions for area
  chart
    .area()
    .tooltip({
      title: '',
      items: [
        {
          field: 'share',
          name: 'Share',
          valueFormatter: '.0%',
        },
      ],
    })
    .style('fillOpacity', (items: VisualPredictionItem[]) =>
      items[0].name === player.username ? 1 : 0.3
    )
    .style('fill', (items: VisualPredictionItem[]) =>
      items[0].name === player.username
        ? `linear-gradient(-90deg, rgba(0, 0, 0, 0) 0%, ${CssVariables['--accent-color']} 100%)`
        : `linear-gradient(-90deg, rgba(0, 0, 0, 0) 0%, ${CssVariables['--text-color']} 100%)`
    );

  // Styling & tooltip interactions for line
  chart
    .line()
    .tooltip(false)
    .style('stroke', (items: VisualPredictionItem[]) =>
      items[0].name === player.username
        ? CssVariables['--accent-color']
        : CssVariables['--text-color']
    );

  // Updating tooltip position
  chart.interaction('tooltip', {
    position: 'left', // Matching the positioning of map's tooltip
  });

  // Disabling legend filtering
  chart.interaction('legendFilter', false);
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

interface VisualPredictionItem {
  name: string;
  turn: number;
  share: number;
  key?: string;
  method: 'PREDICTION' | 'NETWORTH';
}

const getVisualPredictionItems = (
  player: Player,
  players: Player[],
  predictions: Prediction[],
  playerTrends: PlayerTrend[]
): VisualPredictionItem[] => {
  // Utility method to collect by turn and player ID
  const collectByTurnAndPlayerId = <
    T extends { playerId: number; turn: number }
  >(
    list: T[]
  ): Map<number, Map<number, T>> => {
    return list.reduce((turnMap, element) => {
      const playerMap = turnMap.get(element.turn) || new Map<number, T>();
      playerMap.set(element.playerId, element);
      turnMap.set(element.turn, playerMap);
      return turnMap;
    }, new Map<number, Map<number, T>>());
  };

  // Utility method to add dummy player record if missing
  const addMissingPlayers = <T extends { playerId: number; turn: number }>(
    map: Map<number, Map<number, T>>,
    dummy: T
  ): void => {
    map.forEach((playerMap, turn) => {
      players.forEach((p) => {
        if (playerMap.has(p.id)) return;
        playerMap.set(p.id, {
          ...dummy,
          playerId: p.id,
          turn,
        });
      });
    });
  };

  // Extracting turn-wise player trends
  const turnWisePlayerTrends = collectByTurnAndPlayerId(playerTrends);
  addMissingPlayers(turnWisePlayerTrends, {
    cash: 0,
    netWorth: 0,
    playerId: -1,
    turn: -1,
  });

  // Extracting turn-wise predictions
  const turnWisePredictions = collectByTurnAndPlayerId(predictions);
  addMissingPlayers(turnWisePredictions, {
    winnerProbability: 0,
    bankruptProbability: 1,
    type: 'BANKRUPT',
    playerId: -1,
    turn: -1,
  });

  // Calculating maximum turns for the current game
  const maxTurns = Array.from(turnWisePlayerTrends.keys()).reduce(
    (max, turn) => Math.max(turn, max),
    0
  );

  // Utility method to find the visualization method
  const getVisualizationMethod = (
    playerPredictionMap?: Map<number, Prediction>
  ): 'NETWORTH' | 'PREDICTION' => {
    if (!playerPredictionMap) return 'NETWORTH';
    const playerPredictions = Array.from(playerPredictionMap.values());
    playerPredictions.sort(
      (p1, p2) => p2.winnerProbability - p1.winnerProbability
    );
    return playerPredictions.length > 1 &&
      playerPredictions[0].type === 'WINNER' &&
      Math.abs(
        playerPredictions[0].winnerProbability -
          playerPredictions[1].winnerProbability
      ) >= 0.01
      ? 'PREDICTION'
      : 'NETWORTH';
  };

  // Preparing visual prediction items
  const items: VisualPredictionItem[] = [];
  for (let turn = 1; turn <= maxTurns; turn++) {
    // Determining whether to use NETWORTH or PREDICTION method for visualization
    const playerPredictionMap = turnWisePredictions.get(turn);
    const playerTrendMap = turnWisePlayerTrends.get(turn);
    const method = getVisualizationMethod(playerPredictionMap);

    // Calculating total share size in the current turn
    const totalShareSize =
      method === 'PREDICTION'
        ? Array.from(playerPredictionMap?.values() || []).reduce(
            (share, { winnerProbability }) => share + winnerProbability,
            0
          )
        : Array.from(playerTrendMap?.values() || []).reduce(
            (share, { netWorth }) => share + netWorth,
            0
          );

    // Figuring visual prediction item for other players
    const otherItem: VisualPredictionItem = {
      name: 'Others',
      turn,
      key: `Others-${turn}`,
      share: 0,
      method,
    };
    players
      .filter((p) => p.id !== player.id)
      .forEach((p) => {
        otherItem.share +=
          method === 'PREDICTION'
            ? playerPredictionMap?.get(p.id)?.winnerProbability || 0
            : playerTrendMap?.get(p.id)?.netWorth || 0;
      });
    otherItem.share /= totalShareSize;

    // Figuring visual prediction item for requested player
    const playerItem: VisualPredictionItem = {
      name: player.username,
      turn,
      key: `${player.username}-${turn}`,
      share: 0,
      method,
    };
    playerItem.share =
      method === 'PREDICTION'
        ? playerPredictionMap?.get(player.id)?.winnerProbability || 0
        : playerTrendMap?.get(player.id)?.netWorth || 0;
    playerItem.share /= totalShareSize;

    items.push(playerItem, otherItem);
  }
  return items;
};
