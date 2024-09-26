import { useEffect, useState } from 'react';
import { confetti } from 'tsparticles-confetti';
import { IConfettiOptions } from 'tsparticles-confetti/types/IConfettiOptions';
import { useTheme } from '../theme';

export interface ConfettiProps {
  type: 'single' | 'multiple';
  interval: number;
}

const defaultProps: ConfettiProps = {
  type: 'single',
  interval: 200,
};

export const Confetti = (props: Partial<ConfettiProps>) => {
  const { accentColor, textColor } = useTheme();
  const [count, setCount] = useState(0);
  const type = props.type || defaultProps.type;
  const interval = props.interval || defaultProps.interval;

  // setting onfocus to resume confetti
  window.onfocus = () => setCount(count + 1);

  useEffect(() => {
    // rendering confetti
    confetti(
      'confetti-container',
      getConfiguration(type, count, [accentColor, textColor])
    );

    // use effect will re-trigger when count state changes
    if (type === 'multiple' && document.hasFocus()) {
      setTimeout(() => setCount(count + 1), interval);
    }
  }, [count, type, interval, accentColor, textColor]);

  return <div id="confetti-container"></div>;
};

const getConfiguration = (
  type: 'single' | 'multiple' = 'single',
  count: number,
  colors: string[]
): Partial<IConfettiOptions> => {
  const baseConfig: Partial<IConfettiOptions> = {
    shapes: ['star', 'square', 'circle', 'polygon', 'diamonds'],
    colors,
  };

  const config = { ...baseConfig };
  switch (type) {
    case 'single':
      return config;
    case 'multiple':
      return {
        ...baseConfig,
        spread: 25 * (count % 4),
        drift: 1,
        decay: 1,
        startVelocity: 5,
        gravity: 0.5,
        count: 75,
        position: { x: 20 * (count % 5), y: -10 * ((count % 2) + 1) },
      };
    default:
      console.error(`Unknown confetti type ${type}`);
      return baseConfig;
  }
};
