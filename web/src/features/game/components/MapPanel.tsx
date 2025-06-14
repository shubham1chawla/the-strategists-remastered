import { useCallback, useEffect, useRef, useState } from 'react';
import cytoscape, { Core, EventObjectNode, Stylesheet } from 'cytoscape';
import popper from 'cytoscape-popper';
import useGame from '@game/hooks/useGame';
import { Land, Player, PlayerLand } from '@game/state';
import useTheme from '@shared/hooks/useTheme';
import { Theme } from '@shared/providers/themeProvider';
import PortfolioModal, { PortfolioModalProps } from './PortfolioModal';
import MapTooltip from './MapTooltip';

const prepareStyles = (theme: Theme): Stylesheet[] => {
  return [
    {
      selector: 'node',
      style: {
        width: 20,
        height: 20,
        color: theme.textColor,
        label: 'data(name)',
        'text-margin-y': -10,
        'text-halign': 'center',
        'text-valign': 'top',
        'text-background-color': theme.darkColor,
        'text-background-opacity': 1,
        'text-background-padding': '4px',
      },
    },
    {
      selector: '.land',
      style: {
        width: 20,
        height: 20,
        shape: 'ellipse',
        'pie-size': '100%',
        ...theme.playerColors.reduce(
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
      selector: '.land-invested',
      style: {
        width: 30,
        height: 30,
      },
    },
    {
      selector: '.prison',
      style: {
        shape: 'pentagon',
        backgroundColor: theme.accentColor,
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
    const investments: Record<string, number> = sortedPlayers.reduce(
      (obj, player, i) =>
        Object.assign(obj, {
          [`${i}`]:
            player.state === 'ACTIVE' && investors[player.id]
              ? investors[player.id].ownership
              : 0,
        }),
      {}
    );
    const classes = [];
    if (
      land.name !== 'Prison' &&
      Object.values(investments).some((value: number) => value > 0)
    ) {
      classes.push('land', 'land-invested');
    } else if (land.name !== 'Prison') {
      classes.push('land');
    } else {
      classes.push('prison');
    }
    cy.add({
      data: {
        land,
        name: land.name,
        id: land.id,
        investments,
      } as any,
      position: { x: land.x, y: land.y },
      selectable: false,
      classes: classes,
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

const preparePlayers = (
  cy: Core,
  lands: Land[],
  players: Player[],
  theme: Theme
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
    const position = {
      x: land.playerPosition.endsWith('left') ? -1 : 1,
      y: land.playerPosition.startsWith('top') ? -1 : 1,
    };

    cy.add({
      data: {
        player,
        name: player.username,
        id: player.username,
        color: theme.getPlayerColor(player),
      } as any,
      position: {
        x: land.x + position.x * counts[player.index]-- * 60,
        y: land.y + position.y * 60,
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
        backgroundColor: theme.textColor,
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
  theme: Theme
): void => {
  if (!cy || !lands.length) return;

  // Clearing previous nodes and edges
  cy.remove(cy.elements());

  // Adding land nodes and edges
  prepareLands(cy, lands, players);

  // Adding player nodes and edges
  preparePlayers(cy, lands, players, theme);

  // Adjusting zoom
  try {
    cy.fit(undefined, Number.MAX_VALUE);
  } catch {
    // Ignore
  }
};

const MapPanel = () => {
  const { players, lands } = useGame();
  const theme = useTheme();

  // Setting up references to DOM elements
  const tooltipRef = useRef<HTMLDivElement | null>(null);

  // Setting up state variables
  const [cy, setCy] = useState<Core | null>(null);
  const [isMapTooltipHidden, setMapTooltipHidden] = useState(true);
  const [hoveredLand, setHoveredLand] = useState<Land | null>(null);
  const [hoveredPlayer, setHoveredPlayer] = useState<Player | null>(null);
  const [modalProps, setModalProps] = useState<PortfolioModalProps | null>(
    null
  );

  // Setting up cytoscape
  const containerRef = useCallback(
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
        style: prepareStyles(theme),
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
        setHoveredLand(land || null);
        setHoveredPlayer(player || null);
        target.popper({
          content: () => tooltipRef.current as HTMLDivElement,
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
    [theme]
  );

  // Setting up popper js support for tooltips
  useEffect(() => cytoscape.use(popper), []);

  // Setting up map elements
  useEffect(
    () => prepareMap(cy, lands, players, theme),
    [cy, players, lands, theme]
  );

  return (
    <>
      <MapTooltip
        tooltipRef={tooltipRef}
        hidden={isMapTooltipHidden}
        land={hoveredLand}
        player={hoveredPlayer}
      />
      <PortfolioModal {...modalProps} />
      <div ref={containerRef} className="strategists-map"></div>
    </>
  );
};

export default MapPanel;
