import { useEffect, useMemo, useState } from 'react';
import { loadFull } from 'tsparticles';
import {
  MoveDirection,
  OutMode,
  type ISourceOptions,
  StartValueType,
} from '@tsparticles/engine';
import Particles, { initParticlesEngine } from '@tsparticles/react';
import useTheme from '@shared/hooks/useTheme';
import useGameState from '@game/hooks/useGameState';

const getSourceOptions = (colors: string[]): ISourceOptions => {
  const baseOptions = {
    preset: 'confetti',
    fpsLimit: 60,
    detectRetina: true,
    smooth: true,
    pauseOnBlur: true,
    pauseOnOutsideViewport: true,
    fullScreen: {
      enable: true,
      zIndex: 1,
    },
    particles: {
      move: {
        decay: 0.1,
        direction: MoveDirection.bottom,
        enable: true,
        gravity: {
          enable: true,
          acceleration: 9.81, // More realistic gravity
        },
        speed: {
          min: 25,
          max: 50,
        },
        outModes: {
          top: OutMode.none,
          default: OutMode.destroy,
        },
      },
      opacity: {
        value: 1,
      },
      number: {
        value: 0,
      },
      rotate: {
        value: {
          min: 0,
          max: 360,
        },
        direction: 'random',
        animation: {
          enable: true,
          speed: 30,
        },
      },
      tilt: {
        direction: 'random',
        enable: true,
        value: {
          min: 0,
          max: 360,
        },
        animation: {
          enable: true,
          speed: 30,
        },
      },
      size: {
        value: 3,
        animation: {
          enable: true,
          startValue: StartValueType.min,
          count: 1,
          speed: 16,
          sync: true,
        },
      },
      roll: {
        darken: {
          enable: true,
          value: 25,
        },
        enlighten: {
          enable: true,
          value: 25,
        },
        enable: true,
        speed: {
          min: 5,
          max: 15,
        },
      },
      wobble: {
        distance: 20,
        enable: true,
        speed: {
          min: -5,
          max: 5,
        },
      },
      shape: {
        type: ['star', 'square', 'circle', 'polygon', 'diamonds'],
        options: {},
      },
      color: {
        value: colors,
      },
    },
    responsive: [
      {
        maxWidth: 1024,
        options: {
          particles: {
            move: {
              speed: {
                min: 33,
                max: 66,
              },
            },
          },
          emitters: [
            {
              position: {
                x: 0,
                y: 0,
              },
              rate: {
                quantity: 2,
                delay: 0.15,
              },
            },
            {
              position: {
                x: 50,
                y: 0,
              },
              rate: {
                quantity: 2,
                delay: 0.15,
              },
            },
            {
              position: {
                x: 100,
                y: 0,
              },
              rate: {
                quantity: 2,
                delay: 0.15,
              },
            },
          ],
        },
      },
    ],
  };

  // Adding emitters
  const directions = [
    MoveDirection.bottomRight,
    MoveDirection.bottom,
    MoveDirection.bottom,
    MoveDirection.bottomLeft,
  ];
  const step = 100 / (directions.length - 1);
  const xPositions = Array.from(
    { length: directions.length },
    (_, i) => i * step,
  );
  const emitters = directions.map((direction, i) => ({
    position: {
      x: xPositions[i],
      y: -20,
    },
    rate: {
      quantity: 2,
      delay: 0.1,
    },
    particles: {
      move: {
        direction,
      },
    },
  }));
  return {
    ...baseOptions,
    emitters,
  };
};

function WinnerConfettiBackdrop() {
  const { winnerPlayer } = useGameState();
  const { accentColor, textColor } = useTheme();
  const [init, setInit] = useState(false);

  useEffect(() => {
    initParticlesEngine(async (engine) => {
      await loadFull(engine);
    }).then(() => {
      setInit(true);
    });
  }, []);

  const options = useMemo(
    () => getSourceOptions([accentColor, textColor]),
    [accentColor, textColor],
  );

  return init && !!winnerPlayer ? (
    <Particles id="confetti-container" options={options} />
  ) : null;
}

export default WinnerConfettiBackdrop;
