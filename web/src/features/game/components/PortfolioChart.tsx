import { useEffect } from 'react';
import { Chart } from '@antv/g2';
import usePortfolioItems from '@game/hooks/usePortfolioItems';
import ChartInterpretationHelp from '@shared/components/ChartInterpretationHelp';
import EmptyContainer from '@shared/components/EmptyContainer';
import useChartTheme from '@shared/hooks/useChartTheme';
import { PortfolioTableProps } from './PortfolioTable';

interface PortfolioChartProps extends PortfolioTableProps {
  showHelp?: boolean;
}

const PortfolioChart = (props: PortfolioChartProps) => {
  const { playerLands, perspective, showHelp } = props;
  const chartTheme = useChartTheme();
  const portfolioItems = usePortfolioItems(perspective, playerLands);

  useEffect(() => {
    // Creating chart's instance
    const chart = new Chart({
      container: 'portfolio-container',
      height: portfolioItems.length * 80 || 100,
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
      theme: chartTheme,
    });

    // Setting up chart's data
    chart
      .point()
      .data(portfolioItems)
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
        items: [
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
        ],
      });

    // Updating tooltip position
    chart.interaction('tooltip', {
      position: 'left', // Matching the positioning of map's tooltip
    });

    // Rendering the chart
    chart.render();
  }, [portfolioItems, perspective, playerLands, chartTheme]);

  if (!playerLands.length) {
    return <EmptyContainer message="No investments available!" />;
  }
  return (
    <div className="strategists-viz">
      <div id="portfolio-container"></div>
      {showHelp && (
        <ChartInterpretationHelp
          message={
            perspective === 'player'
              ? "The chart highlights the player's investments across various properties. A larger circle represents a significant investment amount."
              : "The chart highlights the property's investors. A larger circle represents a significant investment amount by the investor."
          }
        />
      )}
    </div>
  );
};

export default PortfolioChart;
