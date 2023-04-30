import cytoscape, {
  Core,
  CytoscapeOptions,
  EventObjectNode,
  Stylesheet,
} from 'cytoscape';
import { useEffect, useRef } from 'react';
import { useSelector } from 'react-redux';
import { strategistsColors } from '../App';
import { Land } from '../redux';

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

const updateMap = (cy: Core, lands: Land[]): void => {
  if (!cy || !lands.length) {
    return;
  }

  // clearing previous nodes and edges
  cy.remove(cy.elements());

  // adding lands' nodes
  lands.forEach((land) => {
    cy.add({
      data: { name: land.name, id: land.id } as any,
      position: { x: land.x, y: land.y },
      selectable: false,
      classes: land.name === 'Prison' ? 'prison' : 'node',
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
  const { lands } = useSelector((state: any) => state.lobby);
  const container = useRef<HTMLDivElement>(null);

  const onNodeClick = (event: EventObjectNode): void => {
    alert(event.target.data());
  };

  useEffect(() => {
    // Creating dynamic style for Prison node
    const prisonStyle: Stylesheet = {
      selector: '.prison',
      style: {
        shape: 'pentagon',
        backgroundColor: strategistsColors['--accent-color'],
      },
    };

    // Setting up cytoscape
    const cy = cytoscape({
      container: container.current,
      ...baseOptions,
      style: [...baseStyles, prisonStyle],
    });
    cy.on('click', 'node', onNodeClick);

    // Setting up map elements
    updateMap(cy, lands);
  }, [lands]);

  return <div ref={container} className="strategists-map"></div>;
};
