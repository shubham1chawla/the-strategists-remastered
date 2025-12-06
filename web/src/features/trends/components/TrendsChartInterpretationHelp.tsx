import ChartInterpretationHelp from '@shared/components/ChartInterpretationHelp';

interface TrendsChartInterpretationHelpProps {
  perspective: 'player' | 'land';
}

function TrendsChartInterpretationHelp({
  perspective,
}: TrendsChartInterpretationHelpProps) {
  return (
    <ChartInterpretationHelp
      message={
        perspective === 'player'
          ? "The chart highlights the change in player's cash and net worth per step."
          : "The chart highlights the change in the land's market value per step."
      }
    />
  );
}

export default TrendsChartInterpretationHelp;
