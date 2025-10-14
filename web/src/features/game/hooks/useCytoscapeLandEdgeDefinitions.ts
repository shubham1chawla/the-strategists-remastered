import { useMemo } from 'react';
import { EdgeDefinition } from 'cytoscape';
import useGameState from './useGameState';

const useCytoscapeLandEdgeDefinitions = () => {
  const { lands } = useGameState();

  // Create land to land edges
  const landEdges = useMemo(() => {
    const edges: EdgeDefinition[] = [];

    if (!lands.length) return edges;

    // Adding n-1 edges
    for (let i = 1; i < lands.length; i += 1) {
      const prev = lands[i - 1];
      const curr = lands[i];
      edges.push({
        data: {
          id: `${prev.id}->${curr.id}`,
          source: `${prev.id}`,
          target: `${curr.id}`,
        },
        selectable: false,
        classes: 'land-edge',
      });
    }

    // Adding last edge
    edges.push({
      data: {
        id: `${lands[lands.length - 1].id}->${lands[0].id}`,
        source: `${lands[lands.length - 1].id}`,
        target: `${lands[0].id}`,
      },
      selectable: false,
      classes: 'land-edge',
    });

    return edges;
  }, [lands]);

  return landEdges;
};

export default useCytoscapeLandEdgeDefinitions;
