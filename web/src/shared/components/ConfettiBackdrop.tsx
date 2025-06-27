import { useEffect, useState } from 'react';
import { confetti } from 'tsparticles-confetti';
import { IConfettiOptions } from 'tsparticles-confetti/types/IConfettiOptions';
import useTheme from '@shared/hooks/useTheme';

interface ConfettiBackdropProps {
  type: 'single' | 'multiple';
  interval: number;
}

const defaultProps: ConfettiBackdropProps = {
  type: 'single',
  interval: 200,
};

const getConfiguration = (
  type: 'single' | 'multiple',
  count: number,
  colors: string[],
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
      // eslint-disable-next-line no-console
      console.error(`Unknown confetti type ${type}`);
      return baseConfig;
  }
};

function ConfettiBackdrop(props: Partial<ConfettiBackdropProps>) {
  const { accentColor, textColor } = useTheme();
  const [count, setCount] = useState(0);
  const { type = defaultProps.type, interval = defaultProps.interval } = props;

  // setting onfocus to resume confetti
  window.onfocus = () => setCount(count + 1);

  useEffect(() => {
    // rendering confetti
    confetti(
      'confetti-container',
      getConfiguration(type, count, [accentColor, textColor]),
    );

    // use effect will re-trigger when count state changes
    if (type === 'multiple' && document.hasFocus()) {
      setTimeout(() => setCount(count + 1), interval);
    }
  }, [count, type, interval, accentColor, textColor]);

  return <div id="confetti-container" />;
}

export default ConfettiBackdrop;
