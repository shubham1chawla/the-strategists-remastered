import { useEffect } from 'react';
import { Chart } from '@antv/g2';
import ChartInterpretationHelp from '@shared/components/ChartInterpretationHelp';
import EmptyContainer from '@shared/components/EmptyContainer';
import useChartTheme from '@shared/hooks/useChartTheme';
import useTheme from '@shared/hooks/useTheme';
import { Theme } from '@shared/providers/themeProvider';
import useTrendsState from '@trends/hooks/useTrendsState';
import { LandTrend, PlayerTrend } from '@trends/state';

const drawPlayerTrends = (
  chart: Chart,
  trends: PlayerTrend[],
  theme: Theme,
) => {
  // Adding area marks
  chart
    .area()
    .data(trends)
    .encode('x', 'step')
    .encode('y', 'netWorth')
    .scale('y', { domainMin: 0 })
    .style(
      'fill',
      `linear-gradient(-90deg, rgba(0, 0, 0, 0) 0%, ${theme.accentColor} 100%)`,
    )
    .tooltip(false);

  // Adding line marks
  chart
    .line()
    .data(trends)
    .encode('x', 'step')
    .encode('y', 'netWorth')
    .scale('y', { domainMin: 0 })
    .style('stroke', theme.accentColor)
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
    .encode('x', 'step')
    .encode('y', 'cash')
    .scale('y', { domainMin: 0 })
    .style('stroke', theme.textColor)
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

const drawLandTrends = (chart: Chart, trends: LandTrend[], theme: Theme) => {
  // Adding area marks
  chart
    .area()
    .data(trends)
    .encode('x', 'step')
    .encode('y', 'marketValue')
    .scale('y', { domainMin: 0 })
    .style(
      'fill',
      `linear-gradient(-90deg, rgba(0, 0, 0, 0) 0%, ${theme.accentColor} 100%)`,
    )
    .tooltip({
      title: '',
      items: [
        {
          name: 'Step',
          field: 'step',
          color: 'transparent',
        },
      ],
    });

  // Adding line marks
  chart
    .line()
    .data(trends)
    .encode('x', 'step')
    .encode('y', 'marketValue')
    .scale('y', { domainMin: 0 })
    .style('stroke', theme.accentColor)
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

interface TrendsChartProps {
  perspective: 'player' | 'land';
  id: number;
  showHelp?: boolean;
}

function TrendsChart(props: TrendsChartProps) {
  const theme = useTheme();
  const chartTheme = useChartTheme();
  const { playerTrends, landTrends } = useTrendsState();
  const { perspective, id, showHelp } = props;

  useEffect(() => {
    if (
      (perspective === 'land' && !landTrends.length) ||
      (perspective === 'player' && !playerTrends.length)
    )
      return;

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
      theme: chartTheme,
    });

    // Updating tooltip position
    chart.interaction('tooltip', {
      position: 'left', // Matching the positioning of map's tooltip
    });

    switch (perspective) {
      case 'player':
        drawPlayerTrends(
          chart,
          playerTrends.filter(({ playerId }) => playerId === id),
          theme,
        );
        break;
      case 'land':
        drawLandTrends(
          chart,
          landTrends.filter(({ landId }) => landId === id),
          theme,
        );
        break;
      default:
        throw new Error(`Unrecognized perspective: ${perspective}!`);
    }
    chart.render();
  }, [id, perspective, playerTrends, landTrends, theme, chartTheme]);

  if (
    (perspective === 'land' && !landTrends.length) ||
    (perspective === 'player' && !playerTrends.length)
  ) {
    return <EmptyContainer message="No trends available!" />;
  }
  return (
    <div className="strategists-viz">
      <div id="trends-container" />
      {showHelp && (
        <ChartInterpretationHelp
          message={
            perspective === 'player'
              ? "The chart highlights the change in player's cash and net worth per step."
              : "The chart highlights the change in the land's market value per step."
          }
        />
      )}
    </div>
  );
}

export default TrendsChart;
