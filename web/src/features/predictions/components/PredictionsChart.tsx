import { useEffect } from 'react';
import { Chart } from '@antv/g2';
import ChartInterpretationHelp from '@shared/components/ChartInterpretationHelp';
import EmptyContainer from '@shared/components/EmptyContainer';
import { Player } from '@game/state';
import usePredictions from '@predictions/hooks/usePredictions';
import usePredictionsChartItems from '@predictions/hooks/usePredictionsChartItems';
import useChartTheme from '@shared/hooks/useChartTheme';
import useTheme from '@shared/hooks/useTheme';

interface PredictionsChartProps {
  player: Player;
  showHelp?: boolean;
}

const PredictionsChart = ({ player, showHelp }: PredictionsChartProps) => {
  const predictions = usePredictions();
  const theme = useTheme();
  const chartTheme = useChartTheme();
  const predictionsChartItems = usePredictionsChartItems(player);

  useEffect(() => {
    if (!predictions.length) return;

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
          tickCount: 3,
          labelFormatter: '.0%',
        },
      },
      scale: {
        color: {
          range: [theme.textColor, theme.accentColor],
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
            label === 'Opponents'
              ? "Opponents' winning probability"
              : `${player.username}'s winning probability`,
        },
      },
      theme: chartTheme,
    });

    // Adding data to the chart's instance
    chart.data(predictionsChartItems);

    // Drawing area mark for difference
    chart
      .area()
      .data({
        transform: [
          {
            type: 'fold',
            fields: ['Opponents', player.username],
            key: 'side',
            value: 'share',
          },
        ],
      })
      .transform([{ type: 'diffY' }])
      .encode('x', 'turn')
      .encode('y', 'share')
      .encode('color', 'side')
      .tooltip({
        title: '',
        items: [
          {
            channel: 'y',
            valueFormatter: '.0%',
          },
        ],
      });

    // Drawing player's line for reference
    chart
      .line()
      .encode('x', 'turn')
      .encode('y', player.username)
      .style('stroke', theme.accentColor)
      .tooltip(false);

    // Updating tooltip position
    chart.interaction('tooltip', {
      position: 'left', // Matching the positioning of map's tooltip
    });

    // Disabling legend filtering
    chart.interaction('legendFilter', false);

    // Rendering chart
    chart.render();
  }, [player, predictions, theme, chartTheme, predictionsChartItems]);

  if (!predictions.length) {
    return <EmptyContainer message="No predictions available!" />;
  }
  return (
    <div className="strategists-viz">
      <div id="predictions-container"></div>
      {showHelp && (
        <ChartInterpretationHelp
          message={`The chart highlights the change in winning probabilities of ${player.username} compared to opponents per turn. A larger area represents a stark contrast in the chance of winning for any side.`}
        />
      )}
    </div>
  );
};

export default PredictionsChart;
