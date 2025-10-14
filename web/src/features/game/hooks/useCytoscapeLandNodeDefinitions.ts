import { useMemo } from 'react';
import { NodeDefinition } from 'cytoscape';
import { PlayerLand } from '@game/state';
import useGameState from './useGameState';

const useCytoscapeLandNodeDefinitions = () => {
  const { players, lands } = useGameState();

  // Sorting players based on ID to assign them colors uniformly
  const sortedPlayers = useMemo(
    () => [...players].sort((a, b) => a.id - b.id),
    [players],
  );

  // Creating land nodes
  const landNodes = useMemo(
    () =>
      lands.map((land): NodeDefinition => {
        // Creating an object-based map for Player ID -> PlayerLand
        const investors: Record<number, PlayerLand> = land.players.reduce(
          (obj, pl) =>
            Object.assign(obj, {
              [pl.playerId || -1]: pl,
            }),
          {},
        );

        // Creating an object-based map for Player ID -> Ownership
        const investments: Record<string, number> = sortedPlayers.reduce(
          (obj, player, i) =>
            Object.assign(obj, {
              [`${i}`]:
                player.state === 'ACTIVE' && investors[player.id]
                  ? investors[player.id].ownership
                  : 0,
            }),
          {},
        );

        // Computing cytoscape-based CSS class names for node
        const classes = [];
        if (land.name === 'Prison') {
          classes.push('prison');
        } else if (
          Object.values(investments).some((value: number) => value > 0)
        ) {
          classes.push('land', 'land-invested');
        } else {
          classes.push('land');
        }

        return {
          data: {
            land,
            name: land.name,
            id: `${land.id}`,
            investments,
          },
          position: { x: land.x, y: land.y },
          selectable: false,
          classes,
        };
      }),
    [lands, sortedPlayers],
  );

  return landNodes;
};

export default useCytoscapeLandNodeDefinitions;
