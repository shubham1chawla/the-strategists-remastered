import ChartInterpretationHelp from '@shared/components/ChartInterpretationHelp';

interface PortfolioChartInterpretationHelpProps {
  perspective: 'player' | 'land';
}

function PortfolioChartInterpretationHelp({
  perspective,
}: PortfolioChartInterpretationHelpProps) {
  return (
    <ChartInterpretationHelp
      message={
        perspective === 'player'
          ? "The chart highlights the player's investments across various properties. A larger circle represents a significant investment amount."
          : "The chart highlights the property's investors. A larger circle represents a significant investment amount by the investor."
      }
    />
  );
}

export default PortfolioChartInterpretationHelp;
