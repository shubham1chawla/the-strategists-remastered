import { useMemo } from 'react';
import { NodeDefinition } from 'cytoscape';
import useTheme from '@shared/hooks/useTheme';
import useGame from './useGame';

const useCytoscapePlayerNodeDefinitions = () => {
  const { getPlayerColor } = useTheme();
  const { players, lands } = useGame();

  // Creating player nodes
  const playerNodes = useMemo(() => {
    // Creating players count per land array to space players uniformly
    // This can not be in its own 'useMemo' since we will be mutating this array below!
    const playersCountPerLandIndex: number[] = players.reduce(
      (counts, player) => {
        if (player.state !== 'BANKRUPT') {
          counts[player.index]++;
        }
        return counts;
      },
      Array((lands || []).length).fill(0),
    );

    const nodes: NodeDefinition[] = [];
    for (const player of players || []) {
      // Not rendering bankrupted player's node
      if (player.state === 'BANKRUPT') continue;

      // Player's current land
      const land = lands[player.index];
      const position = {
        x: land.playerPosition.endsWith('left') ? -1 : 1,
        y: land.playerPosition.startsWith('top') ? -1 : 1,
      };

      nodes.push({
        data: {
          player,
          name: player.username,
          id: player.username,
          color: getPlayerColor(player),
        },
        position: {
          x:
            land.x + position.x * playersCountPerLandIndex[player.index]-- * 60,
          y: land.y + position.y * 60,
        },
        selectable: false,
        classes: 'player',
      });
    }
    return nodes;
  }, [getPlayerColor, lands, players]);

  return playerNodes;
};

export default useCytoscapePlayerNodeDefinitions;
