import {
  createContext,
  PropsWithChildren,
  useCallback,
  useEffect,
  useMemo,
} from 'react';
import { useSelector } from 'react-redux';
import { ConfigProvider } from 'antd';
import { scaleOrdinal, schemeSet1 } from 'd3';
import { identicon, shapes } from '@dicebear/collection';
import { createAvatar } from '@dicebear/core';
import { StrategistsState } from '@/store';
import EmptyContainer from '@shared/components/EmptyContainer';
import useLoginState from '@login/hooks/useLoginState';

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
  '--border-color-rgba': 'rgba(245, 246, 250, 0.06)', // text-color-rgb + alpha
} as const;

/**
 * Player colors are defined from D3 library
 * Making first color same as Strategists default theme color
 */
const allPlayerColors = schemeSet1.map((color, i) =>
  i === 0 ? DefaultCssVariables['--accent-color'] : color,
);

export interface Theme {
  darkColor: string;
  textColor: string;
  accentColor: string;
  playerColors: string[];
  getPlayerColor: (username: string) => string;
  getPlayerAvatarDataUri: (username: string) => string;
  getLandAvatarDataUri: (name: string) => string;
}

export const ThemeContext = createContext<Theme | null>(null);

function ThemeProvider({ children }: PropsWithChildren) {
  const { player: loggedInPlayer } = useLoginState();
  const { players } = useSelector((state: StrategistsState) => state.gameState);

  const playersLength = useMemo(() => players?.length || 0, [players]);

  const playerColors = useMemo(
    () => allPlayerColors.slice(0, playersLength),
    [playersLength],
  );

  const sortedPlayers = useMemo(
    () => [...players].sort((a, b) => a.id - b.id),
    [players],
  );

  const getPlayerColor = useCallback(
    (username: string) => {
      if (!sortedPlayers || !sortedPlayers.length) {
        return DefaultCssVariables['--accent-color'];
      }
      const scale = scaleOrdinal(playerColors).domain(
        sortedPlayers.map((player) => player.username),
      );
      return scale(username);
    },
    [playerColors, sortedPlayers],
  );

  const getPlayerAvatarDataUri = useCallback(
    (username: string) => {
      const avatar = createAvatar(identicon, {
        backgroundColor: [getPlayerColor(username).substring(1)],
        rowColor: [DefaultCssVariables['--text-color'].substring(1)],
        seed: username,
        size: 20,
      });
      return avatar.toDataUri();
    },
    [getPlayerColor],
  );

  const accentColor = useMemo(
    () =>
      loggedInPlayer
        ? getPlayerColor(loggedInPlayer.username)
        : DefaultCssVariables['--accent-color'],
    [loggedInPlayer, getPlayerColor],
  );

  const getLandAvatarDataUri = useCallback((name: string) => {
    const colors = allPlayerColors.slice(0, 6).map((hex) => hex.substring(1));
    const avatar = createAvatar(shapes, {
      backgroundColor: [DefaultCssVariables['--text-color'].substring(1)],
      shape1Color: colors,
      shape2Color: colors,
      shape3Color: colors,
      seed: name,
      size: 20,
    });
    return avatar.toDataUri();
  }, []);

  const variables = useMemo(
    () => ({
      ...DefaultCssVariables,
      '--accent-color': accentColor,
    }),
    [accentColor],
  );

  // Setting up documents theme colors
  useEffect(() => {
    Object.entries(variables).forEach(([key, value]) =>
      document.documentElement.style.setProperty(key, value),
    );
  }, [variables]);

  // Creating theme context value
  const value: Theme = useMemo(
    () => ({
      darkColor: DefaultCssVariables['--dark-color'],
      textColor: DefaultCssVariables['--text-color'],
      accentColor,
      playerColors,
      getPlayerColor,
      getPlayerAvatarDataUri,
      getLandAvatarDataUri,
    }),
    [
      accentColor,
      playerColors,
      getPlayerColor,
      getPlayerAvatarDataUri,
      getLandAvatarDataUri,
    ],
  );

  return (
    <ThemeContext.Provider value={value}>
      <ConfigProvider
        renderEmpty={() => <EmptyContainer />}
        theme={{
          components: {
            Collapse: {
              headerBg: 'transparent',
              colorBorder: variables['--border-color-rgba'],
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
            colorBorderDisabled: variables['--accent-color'],
            colorErrorBg: 'transparent',
            colorWarningBg: 'transparent',
            colorSuccessBg: 'transparent',
            colorText: variables['--text-color'],
            colorTextPlaceholder: variables['--text-color'],
            colorTextBase: variables['--text-color'],
            colorInfo: variables['--accent-color'],
            colorInfoBg: 'transparent',
            colorPrimaryBg: 'transparent',
            colorBorderSecondary: variables['--border-color-rgba'],
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
}

export default ThemeProvider;
