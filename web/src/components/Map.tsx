import axios from 'axios';
import cytoscape, { Core, CytoscapeOptions, Stylesheet } from 'cytoscape';
import { useEffect, useState } from 'react';

const baseOptions: CytoscapeOptions = {
  autolock: true,
  userPanningEnabled: true,
  userZoomingEnabled: true,
  maxZoom: 1.5,
  minZoom: 0.75,
  boxSelectionEnabled: false,
  pixelRatio: 'auto',
  layout: {
    name: 'preset',
  },
};

const baseStyles: Stylesheet[] = [
  {
    selector: 'node',
    style: {
      width: 20,
      height: 20,
      shape: 'ellipse',
      color: 'white',
      label: 'data(name)',
    },
  },
  {
    selector: 'edge',
    style: {
      width: 2,
      'curve-style': 'bezier',
      'target-arrow-shape': 'triangle',
    },
  },
];

const updateMap = (cy: Core, lands: any[]): void => {
  // clearing previous nodes and edges
  cy.remove(cy.elements());

  // adding lands' nodes
  lands.forEach((land) => {
    cy.add({
      data: { land, name: land.name, id: land.id },
      position: { x: land.x, y: land.y },
      selectable: false,
      classes: 'node',
    });
  });

  // adding n-1 edges
  for (let i = 1; i < lands.length; i++) {
    const prev = lands[i - 1];
    const curr = lands[i];
    cy.add({
      data: {
        id: `${prev.id}->${curr.id}`,
        source: prev.id,
        target: curr.id,
      },
      selectable: false,
      classes: 'edge',
    });
  }

  // adding last edge
  cy.add({
    data: {
      id: `${lands[lands.length - 1].id}->${lands[0].id}`,
      source: lands[lands.length - 1].id,
      target: lands[0].id,
    },
    selectable: false,
    classes: 'edge',
  });

  cy.fit(undefined, Number.MAX_VALUE);
};

export const Map = () => {
  const [lands, setLands] = useState<any[]>([]);

  useEffect(() => {
    // Fetching lands data from the server
    axios.get('/api/lands').then(({ data }) => {
      // updating the state
      setLands(data);

      // Setting up map elements
      updateMap(cy, data);
    });

    // Setting up cytoscape
    const cy = cytoscape({
      container: document.getElementById('cy'),
      ...baseOptions,
      style: [...baseStyles],
    });
  }, []);

  return <div className="strategists-map" id="cy"></div>;
};
