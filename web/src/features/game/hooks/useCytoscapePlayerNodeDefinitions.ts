import { useMemo } from 'react';
import { NodeDefinition } from 'cytoscape';
import useTheme from '@shared/hooks/useTheme';
import useGameState from './useGameState';

const useCytoscapePlayerNodeDefinitions = () => {
  const { getPlayerColor } = useTheme();
  const { players, lands } = useGameState();

  // Creating player nodes
  const playerNodes = useMemo(() => {
    // Creating players count per land array to space players uniformly
    // This can not be in its own 'useMemo' since we will be mutating this array below!
    const playersCountPerLandIndex: number[] = Array(lands.length)
      .fill(0)
      .map(
        (_, i) =>
          players.filter(
            (player) => player.state !== 'BANKRUPT' && player.index === i,
          ).length,
      );

    const nodes: NodeDefinition[] = [];
    players.forEach((player) => {
      // Not rendering bankrupted player's node
      if (player.state === 'BANKRUPT') return;

      // Player's current land
      const land = lands[player.index];
      const position = {
        x: land.playerPosition.endsWith('left') ? -1 : 1,
        y: land.playerPosition.startsWith('top') ? -1 : 1,
      };

      // Adding the node
      nodes.push({
        data: {
          player,
          name: player.username,
          id: player.username,
          color: getPlayerColor(player),
        },
        position: {
          x: land.x + position.x * playersCountPerLandIndex[player.index] * 60,
          y: land.y + position.y * 60,
        },
        selectable: false,
        classes: 'player',
      });

      // Updating the number of players on a land
      playersCountPerLandIndex[player.index] -= 1;
    });
    return nodes;
  }, [getPlayerColor, lands, players]);

  return playerNodes;
};

export default useCytoscapePlayerNodeDefinitions;
