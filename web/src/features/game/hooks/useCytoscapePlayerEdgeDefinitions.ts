import { useMemo } from 'react';
import { EdgeDefinition } from 'cytoscape';
import useGameState from './useGameState';

const useCytoscapePlayerEdgeDefinitions = () => {
  const { players, lands } = useGameState();

  // Computing players count per index for grouping
  const playersCountPerIndex = useMemo(
    () =>
      players
        .filter((player) => player.state !== 'BANKRUPT')
        .reduce((map, player) => {
          map.set(player.index, (map.get(player.index) || 0) + 1);
          return map;
        }, new Map<number, number>()),
    [players],
  );

  // Creating player to land edges
  const playerEdges = useMemo(() => {
    const edges: EdgeDefinition[] = [];
    players.forEach((player) => {
      // Not rendering bankrupted player's edge
      if (player.state === 'BANKRUPT') return;

      const source =
        (playersCountPerIndex.get(player.index) || 0) > 1
          ? `player-group-${player.index}`
          : player.username;
      const target = `${lands[player.index].id}`;
      const id = `${source}->${target}`;

      edges.push({
        data: {
          id,
          source,
          target,
        },
        selectable: false,
        classes: 'player-edge',
      });
    });
    return edges;
  }, [lands, players, playersCountPerIndex]);

  return playerEdges;
};

export default useCytoscapePlayerEdgeDefinitions;
