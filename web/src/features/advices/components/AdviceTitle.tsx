import { Advice } from '@advices/state';

interface AdviceTitleProps {
  advice: Advice;
}

function AdviceTitle({ advice }: AdviceTitleProps) {
  return advice.type
    .split('_')
    .map((s) => `${s[0]}${s.slice(1).toLowerCase()}`)
    .join(' ');
}

export default AdviceTitle;
