import { useEffect } from 'react';
import { Chart } from '@antv/g2';
import EmptyContainer from '@shared/components/EmptyContainer';
import useChartTheme from '@shared/hooks/useChartTheme';
import usePortfolioItems from '@game/hooks/usePortfolioItems';
import { PortfolioTableProps } from './PortfolioTable';

function PortfolioChart({ playerLands, perspective }: PortfolioTableProps) {
  const chartTheme = useChartTheme();
  const portfolioItems = usePortfolioItems(perspective, playerLands);

  useEffect(() => {
    if (!portfolioItems.length) return;

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
            valueFormatter: (value: number) => `${value.toLocaleString()}%`,
          },
          {
            name: 'Investment Amount',
            field: 'buyAmount',
            color: 'transparent',
            valueFormatter: (value: number) => `$${value.toLocaleString()}`,
          },
        ],
      });

    // Updating tooltip position
    chart.interaction('tooltip', {
      position: 'left', // Matching the positioning of map's tooltip
    });

    // Rendering the chart
    chart.render();
  }, [portfolioItems, perspective, chartTheme]);

  if (!portfolioItems.length) {
    return <EmptyContainer message="No investments available!" />;
  }
  return <div id="portfolio-container" />;
}

export default PortfolioChart;
