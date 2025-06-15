import { useContext } from 'react';
import CytoscapeProvider, {
  CytoscapeContext,
} from '@game/providers/cytoscapeProvider';

const useCytoscape = () => {
  const value = useContext(CytoscapeContext);
  if (!value) {
    throw new Error(
      `'${useCytoscape.name}' used outside '${CytoscapeProvider.name}'!`,
    );
  }
  return value;
};

export default useCytoscape;
