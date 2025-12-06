import ChartInterpretationHelp from '@shared/components/ChartInterpretationHelp';
import { Player } from '@game/state';

interface PredictionsChartInterpretationHelpProps {
  player: Player;
}

function PredictionsChartInterpretationHelp({
  player,
}: PredictionsChartInterpretationHelpProps) {
  return (
    <ChartInterpretationHelp
      message={`The chart highlights the change in winning probabilities of ${player.username} compared to opponents per step. A larger area represents a stark contrast in the chance of winning for any side.`}
    />
  );
}

export default PredictionsChartInterpretationHelp;
