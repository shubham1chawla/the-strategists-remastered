import { useMemo } from 'react';
import { Land, Player, PlayerLand } from '@game/state';
import useGameState from './useGameState';

interface PortfolioItem extends PlayerLand {
  name: string;
  key?: number;
}

const usePortfolioItems = (
  perspective: 'land' | 'player',
  playerLands: PlayerLand[],
): PortfolioItem[] => {
  const { players, lands } = useGameState();

  // Converting players to playerMap
  const playerMap = useMemo(
    () =>
      players.reduce((map, player) => {
        map.set(player.id, player);
        return map;
      }, new Map<number, Player>()),
    [players],
  );

  // Converting lands to landMap
  const landMap = useMemo(
    () =>
      lands.reduce((map, land) => {
        map.set(land.id, land);
        return map;
      }, new Map<number, Land>()),
    [lands],
  );

  // Creating portfolio items
  const portfolioItems = useMemo(
    () =>
      playerLands
        .filter((pl) => {
          if (perspective === 'player') return true;
          return (
            !pl.playerId || playerMap.get(pl.playerId)?.state !== 'BANKRUPT'
          );
        })
        .map((pl) => {
          const key = perspective === 'player' ? pl.landId : pl.playerId;
          const name = key
            ? perspective === 'player'
              ? landMap.get(key)?.name
              : playerMap.get(key)?.username
            : undefined;
          return {
            ...pl,
            key,
            name: name || 'Unknown',
          };
        }),
    [perspective, playerLands, playerMap, landMap],
  );

  return portfolioItems;
};

export default usePortfolioItems;
