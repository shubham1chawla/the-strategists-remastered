import { useMemo } from 'react';
import { EdgeDefinition } from 'cytoscape';
import useGameState from './useGameState';

const useCytoscapePlayerEdgeDefinitions = () => {
  const { players, lands } = useGameState();

  // Creating player to land edges
  const playerEdges = useMemo(() => {
    const edges: EdgeDefinition[] = [];
    players.forEach((player) => {
      // Not rendering bankrupted player's edge
      if (player.state === 'BANKRUPT') return;
      edges.push({
        data: {
          id: `${player.username}->${lands[player.index].id}`,
          source: player.username,
          target: `${lands[player.index].id}`,
        },
        selectable: false,
        classes: 'player-edge',
      });
    });
    return edges;
  }, [lands, players]);

  return playerEdges;
};

export default useCytoscapePlayerEdgeDefinitions;
