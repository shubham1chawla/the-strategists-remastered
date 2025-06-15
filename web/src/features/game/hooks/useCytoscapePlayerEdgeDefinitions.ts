import { useMemo } from 'react';
import useGame from './useGame';
import { EdgeDefinition } from 'cytoscape';

const useCytoscapePlayerEdgeDefinitions = () => {
  const { players, lands } = useGame();

  // Creating player to land edges
  const playerEdges = useMemo(
    () =>
      players.map(
        (player): EdgeDefinition => ({
          data: {
            id: `${player.username}->${lands[player.index].id}`,
            source: player.username,
            target: `${lands[player.index].id}`,
          },
          selectable: false,
          classes: 'player-edge',
        }),
      ),
    [lands, players],
  );

  return playerEdges;
};

export default useCytoscapePlayerEdgeDefinitions;
