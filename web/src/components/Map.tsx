import cytoscape, {
  Core,
  EventObjectNode,
  Position,
  Stylesheet,
} from 'cytoscape';
import { useEffect, useMemo, useRef, useState } from 'react';
import { useSelector } from 'react-redux';
import { CssVariables } from '../App';
import { Land, Player, State } from '../redux';
import {
  LandInvestmentModal,
  LandInvestmentModalProps,
  PlayerPortfolioModal,
  PlayerPortfolioModalProps,
} from '.';

const prepareStyles = (): Stylesheet[] => {
  return [
    {
      selector: 'node',
      style: {
        width: 20,
        height: 20,
        shape: 'ellipse',
        color: CssVariables['--text-color'],
        label: 'data(name)',
        'text-margin-y': -10,
        'text-halign': 'center',
        'text-valign': 'top',
        'text-background-color': CssVariables['--dark-color'],
        'text-background-opacity': 1,
        'text-background-padding': '4px',
      },
    },
    {
      selector: '.prison',
      style: {
        shape: 'pentagon',
        backgroundColor: CssVariables['--accent-color'],
      },
    },
    {
      selector: '.player',
      style: {
        shape: 'triangle',
        color: CssVariables['--accent-color'],
        backgroundColor: CssVariables['--accent-color'],
        'font-weight': 'bold',
      },
    },
    {
      selector: '.land-edge',
      style: {
        width: 2,
        'curve-style': 'bezier',
        'target-arrow-shape': 'triangle',
      },
    },
    {
      selector: '.player-edge',
      style: {
        width: 2,
        'curve-style': 'unbundled-bezier',
        'target-arrow-shape': 'triangle',
        'line-style': 'dashed',
      },
    },
  ];
};

const prepareLands = (cy: Core, lands: Land[]): void => {
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
      classes: 'land-edge',
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
    classes: 'land-edge',
  });
};

const calculateMiddle = (lands: Land[]): Position => {
  const middle = { x: 0, y: 0 };
  lands.forEach((land) => {
    middle.x += land.x;
    middle.y += land.y;
  });
  middle.x /= lands.length;
  middle.y /= lands.length;
  return middle;
};

const preparePlayers = (cy: Core, lands: Land[], players: Player[]): void => {
  // finding players per index
  const counts = Array(lands.length).fill(0);
  players.forEach((player) => counts[player.index]++);

  // adding players
  players.forEach((player) => {
    // Checking if player is not bankrupt
    if (player.state === 'BANKRUPT') {
      return;
    }

    // player's current land
    const land = lands[player.index];

    // calculating middle of adjusting lands
    const middle = calculateMiddle([
      lands[(player.index - 1 + lands.length) % lands.length],
      land,
      lands[(player.index + 1) % lands.length],
    ]);

    const node = cy.add({
      data: { player, name: player.username, id: player.username } as any,
      position: {
        x: land.x + (land.x > middle.x ? 1 : -1) * counts[player.index]-- * 60,
        y: land.y + (land.y > middle.y ? 1 : -1) * 60,
      },
      selectable: false,
      classes: 'player',
    });

    // adding player's edge
    cy.add({
      data: {
        id: `${player.username}->${land.id}`,
        source: player.username,
        target: land.id,
      },
      selectable: false,
      classes: 'player-edge',
    });

    // No need to animate waiting players
    if (!player.turn) {
      return;
    }

    // preparing blink animation for current player
    const animation = node.animation({
      duration: 500,
      easing: 'ease',
      position: node.position(),
      renderedPosition: node.position(),
      style: {
        backgroundColor: CssVariables['--text-color'],
      },
    });

    // creating callback for animation to replay alternatingly
    const callback = (): Promise<any> =>
      animation.play().reverse().play().promise('complete').then(callback);
    callback();
  });
};

const prepareMap = (cy: Core, lands: Land[], players: Player[]): void => {
  // clearing previous nodes and edges
  cy.remove(cy.elements());

  // adding land nodes and edges
  prepareLands(cy, lands);

  // adding player nodes and edges
  preparePlayers(cy, lands, players);

  // adjusting zoom
  cy.fit(undefined, Number.MAX_VALUE);
};

export const Map = () => {
  const { players, lands } = useSelector((state: State) => state.lobby);
  const container = useRef<HTMLDivElement>(null);
  const [landInvestmentModalProps, setLandInvestmentModalProps] =
    useState<LandInvestmentModalProps | null>(null);
  const [playerPortfolioModalProps, setPlayerPortfolioModalProps] =
    useState<PlayerPortfolioModalProps | null>(null);

  // memoizing styles since they won't change
  const style = useMemo(prepareStyles, []);

  useEffect(() => {
    if (!lands.length) {
      return;
    }

    // setting up cytoscape
    const cy = cytoscape({
      autolock: true,
      maxZoom: 1.5,
      minZoom: 0.75,
      layout: {
        name: 'preset',
      },
      style,
      container: container.current,
    });

    // adding onclick hook for map modal
    cy.on('click', 'node', (event: EventObjectNode) => {
      const { player, land } = event.target.data();
      if (player) {
        setPlayerPortfolioModalProps({
          open: !!player,
          onCancel: () => setPlayerPortfolioModalProps(null),
          player,
        });
      } else {
        setLandInvestmentModalProps({
          open: !!land,
          onCancel: () => setLandInvestmentModalProps(null),
          land,
        });
      }
    });

    // setting up map elements
    prepareMap(cy, lands, players);
  }, [style, players, lands]);

  return (
    <>
      <LandInvestmentModal {...landInvestmentModalProps} />
      <PlayerPortfolioModal {...playerPortfolioModalProps} />
      <div ref={container} className="strategists-map"></div>
    </>
  );
};
