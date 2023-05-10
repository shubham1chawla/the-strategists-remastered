import cytoscape, {
  Core,
  CytoscapeOptions,
  EventObjectNode,
  Stylesheet,
} from 'cytoscape';
import { useEffect, useRef, useState } from 'react';
import { useSelector } from 'react-redux';
import { strategistsColors } from '../App';
import { Land, Player, State } from '../redux';
import { MapModal, MapModalProps } from '.';

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
      width: 1,
      'curve-style': 'bezier',
      'target-arrow-shape': 'triangle',
    },
  },
  {
    selector: '.player-edge',
    style: {
      width: 1,
      'curve-style': 'unbundled-bezier',
      'target-arrow-shape': 'triangle',
      'line-style': 'dashed',
    },
  },
];

const updateMap = (cy: Core, lands: Land[], players: Player[]): void => {
  if (!cy || !lands.length) {
    return;
  }

  // clearing previous nodes and edges
  cy.remove(cy.elements());

  // adding lands' nodes
  lands.forEach((land) => {
    cy.add({
      data: { land, name: land.name, id: land.id } as any,
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

  // finding players per index
  const counts = Array(lands.length).fill(0);
  players.forEach((player) => counts[player.index]++);

  // adding players
  players.forEach((player) => {
    // adding player's node
    const land = lands[player.index];
    cy.add({
      data: { player, name: player.username, id: player.username } as any,
      position: { x: land.x - counts[player.index]-- * 100, y: land.y - 100 },
      selectable: false,
      classes: 'player',
    });

    // adding player's edge
    cy.add({
      data: {
        id: `${player.username}->${land.id}`,
        source: player.username,
        target: lands[player.index].id,
      },
      selectable: false,
      classes: 'player-edge',
    });
  });

  cy.fit(undefined, Number.MAX_VALUE);
};

export const Map = () => {
  const { players, lands } = useSelector((state: State) => state.lobby);
  const container = useRef<HTMLDivElement>(null);
  const [props, setProps] = useState<MapModalProps | null>(null);

  useEffect(() => {
    // creating dynamic style for Prison node
    const dynamicStyles: Stylesheet[] = [
      {
        selector: '.prison',
        style: {
          shape: 'pentagon',
          backgroundColor: strategistsColors['--accent-color'],
        },
      },
      {
        selector: '.player',
        style: {
          shape: 'triangle',
          backgroundColor: strategistsColors['--accent-color'],
        },
      },
    ];

    // setting up cytoscape
    const cy = cytoscape({
      container: container.current,
      ...baseOptions,
      style: [...baseStyles, ...dynamicStyles],
    });

    // adding onclick hook for map modal
    cy.on('click', 'node', (event: EventObjectNode) => {
      const { player, land } = event.target.data();
      setProps({
        type: player ? 'player' : 'land',
        id: player?.id || land?.id,
        onCancel: () => setProps(null),
      });
    });

    // setting up map elements
    updateMap(cy, lands, players);
  }, [players, lands]);

  return (
    <>
      <MapModal {...props} />
      <div ref={container} className="strategists-map"></div>
    </>
  );
};
