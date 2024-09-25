import { useCallback, useEffect, useRef, useState } from 'react';
import { Alert, Divider } from 'antd';
import cytoscape, {
  Core,
  EventObjectNode,
  Position,
  Stylesheet,
} from 'cytoscape';
import popper from 'cytoscape-popper';
import { CssVariables } from '../App';
import {
  Land,
  Player,
  playerColors,
  PlayerLand,
  useLobby,
  usePlayerColors,
} from '../redux';
import { LandStats, PlayerStats, PortfolioModal, PortfolioModalProps } from '.';

/**
 * -----  MAP COMPONENT BELOW  -----
 */

const prepareStyles = (playersLength: number): Stylesheet[] => {
  return [
    {
      selector: 'node',
      style: {
        width: 20,
        height: 20,
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
      selector: '.land',
      style: {
        width: 30,
        height: 30,
        shape: 'ellipse',
        'pie-size': '95%',
        ...playerColors
          .slice(0, Math.min(playerColors.length, playersLength))
          .reduce(
            (pieStyles, color, i) =>
              Object.assign(pieStyles, {
                [`pie-${i + 1}-background-color`]: color,
                [`pie-${
                  i + 1
                }-background-size`]: `mapData(investments.${i}, 0, 100, 0, 100)`,
              }),
            {}
          ),
      },
    } as Stylesheet,
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
        color: 'data(color)',
        backgroundColor: 'data(color)',
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

const prepareLands = (cy: Core, lands: Land[], players: Player[]): void => {
  // Sorting players based on ID to assign them colors uniformly
  const sortedPlayers = [...players].sort((a, b) => a.id - b.id);

  // Adding lands' nodes
  lands.forEach((land) => {
    const investors: Record<number, PlayerLand> = land.players.reduce(
      (obj, pl) =>
        Object.assign(obj, {
          [pl.playerId || -1]: pl,
        }),
      {}
    );
    const investments = sortedPlayers.reduce(
      (obj, player, i) =>
        Object.assign(obj, {
          [`${i}`]:
            player.state === 'ACTIVE' && investors[player.id]
              ? investors[player.id].ownership
              : 0,
        }),
      {}
    );
    cy.add({
      data: {
        land,
        name: land.name,
        id: land.id,
        investments,
      } as any,
      position: { x: land.x, y: land.y },
      selectable: false,
      classes: land.name === 'Prison' ? 'prison' : 'land',
    });
  });

  // Adding n-1 edges
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

  // Adding last edge
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

const preparePlayers = (
  cy: Core,
  lands: Land[],
  players: Player[],
  getColor: (player: Player) => string
): void => {
  // Finding players per index
  const counts = Array(lands.length).fill(0);
  players.forEach((player) => counts[player.index]++);

  // Adding players
  players.forEach((player) => {
    // Checking if player is not bankrupt
    if (player.state === 'BANKRUPT') {
      return;
    }

    // Player's current land
    const land = lands[player.index];

    // Calculating middle of adjusting lands
    const middle = calculateMiddle([
      lands[(player.index - 1 + lands.length) % lands.length],
      land,
      lands[(player.index + 1) % lands.length],
    ]);

    cy.add({
      data: {
        player,
        name: player.username,
        id: player.username,
        color: getColor(player),
      } as any,
      position: {
        x: land.x + (land.x > middle.x ? 1 : -1) * counts[player.index]-- * 60,
        y: land.y + (land.y > middle.y ? 1 : -1) * 60,
      },
      selectable: false,
      classes: 'player',
    });

    // Adding player's edge
    cy.add({
      data: {
        id: `${player.username}->${land.id}`,
        source: player.username,
        target: land.id,
      },
      selectable: false,
      classes: 'player-edge',
    });
  });

  // Creating callback for animation to replay alternatingly
  const callback = async (): Promise<any> => {
    // Adding animation to the turn player
    const node = cy.filter((ele) => ele.data('player.turn'));
    if (!node || !node.position()) {
      return new Promise((resolve) =>
        setTimeout(() => {
          resolve(callback);
        }, 1000)
      );
    }

    // Preparing blink animation for current player
    const animation = node.animation({
      duration: 1000,
      easing: 'ease',
      position: node.position(),
      renderedPosition: node.position(),
      style: {
        backgroundColor: CssVariables['--text-color'],
      },
    });

    return animation.play().reverse().play().promise('complete').then(callback);
  };
  callback();
};

const prepareMap = (
  cy: Core | null,
  lands: Land[],
  players: Player[],
  getColor: (player: Player) => string
): void => {
  if (!cy || !lands.length) return;

  // Clearing previous nodes and edges
  cy.remove(cy.elements());

  // Adding land nodes and edges
  prepareLands(cy, lands, players);

  // Adding player nodes and edges
  preparePlayers(cy, lands, players, getColor);

  // Adjusting zoom
  try {
    cy.fit(undefined, Number.MAX_VALUE);
  } catch {
    // Ignore
  }
};

export const Map = () => {
  const { players, lands } = useLobby();
  const { getColor } = usePlayerColors();

  // Setting up references to DOM elements
  const tooltip = useRef<HTMLDivElement | null>(null);

  // Setting up state variables
  const [cy, setCy] = useState<Core | null>(null);
  const [isMapTooltipHidden, setMapTooltipHidden] = useState(true);
  const [mapTooltipBodyProps, setMapTooltipBodyProps] =
    useState<Partial<MapTooltipBodyProps> | null>(null);
  const [modalProps, setModalProps] = useState<PortfolioModalProps | null>(
    null
  );

  // Setting up cytoscape
  const container = useCallback(
    (current: HTMLDivElement | null) => {
      if (!current) return;

      // Creating cytoscape instance
      const cy = cytoscape({
        autolock: true,
        maxZoom: 1.5,
        minZoom: 0.75,
        layout: {
          name: 'preset',
        },
        style: prepareStyles(players.length),
        container: current,
      });

      // Adding onclick hook for map modal
      cy.on('click', 'node', (event: EventObjectNode) => {
        const { player, land } = event.target.data();
        setModalProps({
          open: true,
          onCancel: () => setModalProps(null),
          player,
          land,
        });
        setMapTooltipHidden(true);
      });

      // Adding mousemove hook for map tooltip
      cy.on('mousemove', 'node', ({ target }: EventObjectNode) => {
        const { land, player } = target.data();
        setMapTooltipBodyProps({
          land,
          player,
        });
        target.popper({
          content: () => tooltip.current as HTMLDivElement,
          popper: {
            placement: 'right',
            modifiers: [
              {
                name: 'offset',
                options: {
                  offset: [0, 12],
                },
              },
            ],
            strategy: 'absolute',
          },
        });
        setMapTooltipHidden(false);
      });

      // Adding mouseout hook to remove tooltip
      cy.on('mouseout', 'node', (_: EventObjectNode) => {
        setMapTooltipHidden(true);
      });

      // Setting up cytoscape instance's state
      setCy(cy);
    },
    [players.length]
  );

  // Setting up popper js support for tooltips
  useEffect(() => cytoscape.use(popper), []);

  // Setting up map elements
  useEffect(
    () => prepareMap(cy, lands, players, getColor),
    [cy, players, lands, getColor]
  );

  return (
    <>
      <div
        ref={tooltip}
        role="tooltip"
        className={`strategists-map__tooltip ${
          isMapTooltipHidden ? 'strategists-map__tooltip-hidden' : ''
        }`}
      >
        <MapTooltipBody {...mapTooltipBodyProps} />
      </div>
      <PortfolioModal {...modalProps} />
      <div ref={container} className="strategists-map"></div>
    </>
  );
};

/**
 * -----  MAP TOOLTIP BELOW  -----
 */

interface MapTooltipBodyProps {
  player: Player;
  land: Land;
}

const MapTooltipBody = (props: Partial<MapTooltipBodyProps>) => {
  const { player, land } = props;
  const message = player
    ? `Click to check ${player.username}'s portfolio.`
    : land
    ? `Click to check ${land.name}'s investments`
    : null;
  return (
    <>
      {player ? (
        <PlayerStats player={player} />
      ) : land ? (
        <LandStats land={land} />
      ) : null}
      <Divider>
        <Alert type="info" message={message} banner />
      </Divider>
    </>
  );
};
