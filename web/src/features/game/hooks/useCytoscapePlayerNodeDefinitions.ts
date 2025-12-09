import { useMemo } from 'react';
import { NodeDefinition, Position } from 'cytoscape';
import useTheme from '@shared/hooks/useTheme';
import { Land } from '@game/state';
import useGameState from './useGameState';

const RADIUS = 90;
const GROUPED_PLAYER_GAP = 30;

const getPlayerPositionAngle = ({ playerPosition }: Land): number => {
  switch (playerPosition) {
    case 'top':
      return Math.PI / 2;
    case 'top-right':
      return Math.PI / 4;
    case 'right':
      return 0;
    case 'bottom-right':
      return (7 * Math.PI) / 4;
    case 'bottom':
      return (3 * Math.PI) / 2;
    case 'bottom-left':
      return (5 * Math.PI) / 4;
    case 'left':
      return Math.PI;
    case 'top-left':
      return (3 * Math.PI) / 4;
    default:
      throw new Error(`Unknown player position: ${playerPosition}`);
  }
};

const computePlayerPosition = (
  land: Land,
  isGrouped: boolean,
  playersCountOnLand: number,
): Position => {
  // Computing angle based on position text
  const angle = getPlayerPositionAngle(land);

  // Computing x-axis offset from land node
  const isLeft = angle > Math.PI / 2 && angle < (3 * Math.PI) / 2;
  const offsetX =
    Math.cos(angle) * RADIUS +
    (isLeft ? -1 : 1) * (playersCountOnLand - 1) * GROUPED_PLAYER_GAP;

  // Computing y-axis offset from land node
  const offsetY = -1 * Math.sin(angle) * RADIUS;

  // Final position (y-axis is flipped)
  return {
    x: land.x + offsetX,
    y: land.y + offsetY,
  };
};

const useCytoscapePlayerNodeDefinitions = () => {
  const { getPlayerColor, getPlayerAvatarDataUri } = useTheme();
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

    // Creating parent nodes for indexes where more than one player exists
    const playerGroupNodePerIndex = new Map<number, NodeDefinition>();
    playersCountPerLandIndex.forEach((count, index) => {
      if (count > 1) {
        playerGroupNodePerIndex.set(index, {
          data: {
            id: `player-group-${index}`,
          },
        });
      }
    });

    // Adding player group nodes
    const nodes: NodeDefinition[] = [];
    playerGroupNodePerIndex
      .values()
      .forEach((playerGroupNode) => nodes.push(playerGroupNode));

    // Adding player nodes
    players.forEach((player) => {
      // Not rendering bankrupted player's node
      if (player.state === 'BANKRUPT') return;

      // Computing player's position
      const position = computePlayerPosition(
        lands[player.index],
        playerGroupNodePerIndex.has(player.index),
        playersCountPerLandIndex[player.index],
      );

      // Computing classes
      const classes = [];
      if (playerGroupNodePerIndex.has(player.index)) {
        classes.push('grouped-player');
      } else {
        classes.push('ungrouped-player');
      }
      if (player.turn) {
        classes.push('turn-player');
      }

      // Creating player's node
      const node = {
        data: {
          player,
          name: player.username,
          id: player.username,
          color: getPlayerColor(player.username),
          avatarDataUri: getPlayerAvatarDataUri(player.username),
          parent: playerGroupNodePerIndex.get(player.index)?.data.id,
        },
        position,
        selectable: false,
        classes,
      };

      // Adding the node
      nodes.push(node);

      // Updating the number of players on a land
      playersCountPerLandIndex[player.index] -= 1;
    });
    return nodes;
  }, [getPlayerColor, getPlayerAvatarDataUri, lands, players]);

  return playerNodes;
};

export default useCytoscapePlayerNodeDefinitions;
