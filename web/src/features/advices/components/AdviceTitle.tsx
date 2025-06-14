import { Advice } from '@advices/state';

interface AdviceTitleProps {
  advice: Advice;
}

const AdviceTitle = ({ advice }: AdviceTitleProps) => {
  const title = advice.type
    .split('_')
    .map((s) => `${s[0]}${s.slice(1).toLowerCase()}`)
    .join(' ');
  return <>{title}</>;
};

export default AdviceTitle;
