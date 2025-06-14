import { Advice } from '@advices/state';

interface AdviceDescriptionProps {
  advice: Advice;
}

const AdviceDescription = ({ advice }: AdviceDescriptionProps) => {
  const { type, val1, val2 } = advice;
  let description = 'Unknown Advice';
  switch (type) {
    case 'AVOID_TIMEOUT':
      description =
        'The game completed your turn because of inactivity. Please use the "Skip" button!';
      break;
    case 'CONCENTRATE_INVESTMENTS':
      description = `You have invested all over the map; try to have at least ${val1} investments close by!`;
      break;
    case 'FREQUENTLY_INVEST':
      description = `You have yet to invest in the last ${val1} turns. Try investing more to get a competitive edge!`;
      break;
    case 'POTENTIAL_BANKRUPTCY':
      description = `You will go bankrupt if you land on ${val2}! Keep more than $${val1} to avoid bankruptcy.`;
      break;
    case 'SIGNIFICANT_INVESTMENTS':
      description = `Try investing more than ${val1}% to get steep rent from others.`;
      break;
    default:
      console.warn(`Unknown advice type: ${type}`);
  }
  return <>{description}</>;
};

export default AdviceDescription;
