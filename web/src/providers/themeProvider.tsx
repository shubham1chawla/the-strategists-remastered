import {
  createContext,
  PropsWithChildren,
  useCallback,
  useContext,
  useEffect,
  useMemo,
} from 'react';
import { useSelector } from 'react-redux';
import { ConfigProvider } from 'antd';
import { scaleOrdinal, schemeSet1 } from 'd3';
import { Empty } from '../components';
import { Player } from '../features/game/slice';
import { useLogin } from '../hooks';
import { State } from '../store';

/**
 * Theme colors are defined here, all CSS classes should refer to these variables.
 */
const DefaultCssVariables = {
  '--font-family': `'IBM Plex Sans', sans-serif`,
  '--text-color': '#f5f6fa',
  '--text-color-rgb': '245, 246, 250',
  '--dark-color': '#18191a',
  '--dark-color-rgb': '24, 25, 26',
  '--accent-color': '#eb3b5a',
  '--accent-color-hover': '#f7657b',
} as const;

/**
 * Player colors are defined from D3 library
 * Making first color same as Strategists default theme color
 */
const allPlayerColors = schemeSet1.map((color, i) =>
  i === 0 ? DefaultCssVariables['--accent-color'] : color
);

export interface Theme {
  darkColor: string;
  textColor: string;
  accentColor: string;
  playerColors: string[];
  getPlayerColor: (player: Player) => string;
}

const ThemeContext = createContext<Theme | null>(null);

export const useTheme = () => {
  const value = useContext(ThemeContext);
  if (!value) {
    throw new Error(`useTheme hook should only be used inside ThemeProvider!`);
  }
  return value;
};

export const ThemeProvider = ({ children }: PropsWithChildren) => {
  const { player: loggedInPlayer } = useLogin();
  const { players } = useSelector((state: State) => state.game);

  const playersLength = useMemo(() => players?.length || 0, [players]);

  const playerColors = useMemo(
    () => allPlayerColors.slice(0, playersLength),
    [playersLength]
  );

  const sortedPlayers = useMemo(
    () => [...players].sort((a, b) => a.id - b.id),
    [players]
  );

  const getPlayerColor = useCallback(
    (player: Player) => {
      if (!sortedPlayers || !sortedPlayers.length) {
        return DefaultCssVariables['--accent-color'];
      }
      const scale = scaleOrdinal(playerColors).domain(
        sortedPlayers.map(({ username }) => username)
      );
      return scale(player.username);
    },
    [playerColors, sortedPlayers]
  );

  const accentColor = useMemo(
    () =>
      loggedInPlayer
        ? getPlayerColor(loggedInPlayer)
        : DefaultCssVariables['--accent-color'],
    [loggedInPlayer, getPlayerColor]
  );

  const variables = useMemo(
    () => ({
      ...DefaultCssVariables,
      '--accent-color': accentColor,
    }),
    [accentColor]
  );

  // Setting up documents theme colors
  useEffect(() => {
    for (const [key, value] of Object.entries(variables)) {
      document.documentElement.style.setProperty(key, value);
    }
  }, [variables]);

  // Creating theme context value
  const value: Theme = {
    darkColor: DefaultCssVariables['--dark-color'],
    textColor: DefaultCssVariables['--text-color'],
    accentColor,
    playerColors,
    getPlayerColor,
  };

  return (
    <ThemeContext.Provider value={value}>
      <ConfigProvider
        renderEmpty={() => <Empty />}
        theme={{
          components: {
            Collapse: {
              headerBg: 'transparent',
            },
            Select: {
              multipleItemBg: variables['--accent-color'],
            },
            Table: {
              headerBg: 'transparent',
            },
            Tag: {
              defaultBg: 'transparent',
              defaultColor: variables['--text-color'],
            },
          },
          token: {
            borderRadius: 4,
            colorBgBase: variables['--dark-color'],
            colorWhite: variables['--text-color'],
            colorPrimary: variables['--accent-color'],
            colorTextDisabled: variables['--accent-color'],
            colorBorder: variables['--accent-color'],
            colorBgContainer: 'transparent',
            colorErrorBg: 'transparent',
            colorWarningBg: 'transparent',
            colorSuccessBg: 'transparent',
            colorText: variables['--text-color'],
            colorTextPlaceholder: variables['--text-color'],
            colorTextBase: variables['--text-color'],
            colorInfo: variables['--accent-color'],
            colorInfoBg: 'transparent',
            colorPrimaryBg: 'transparent',
            colorBorderSecondary: `rgba(${variables['--text-color-rgb']}, 0.06)`,
            colorBgSpotlight: variables['--dark-color'],
            colorBgContainerDisabled: 'transparent',
            fontFamily: variables['--font-family'],
          },
        }}
      >
        {children}
      </ConfigProvider>
    </ThemeContext.Provider>
  );
};
