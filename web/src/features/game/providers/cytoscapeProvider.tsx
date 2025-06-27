import {
  createContext,
  MutableRefObject,
  PropsWithChildren,
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react';
import cytoscape, { Core, EventObjectNode } from 'cytoscape';
import cytoscapePopper, { PopperFactory } from 'cytoscape-popper';
import {
  autoPlacement,
  computePosition,
  flip,
  limitShift,
  offset,
  shift,
  Strategy,
} from '@floating-ui/dom';
import useTheme from '@shared/hooks/useTheme';
import useCytoscapeLandEdgeDefinitions from '@game/hooks/useCytoscapeLandEdgeDefinitions';
import useCytoscapeLandNodeDefinitions from '@game/hooks/useCytoscapeLandNodeDefinitions';
import useCytoscapePlayerEdgeDefinitions from '@game/hooks/useCytoscapePlayerEdgeDefinitions';
import useCytoscapePlayerNodeDefinitions from '@game/hooks/useCytoscapePlayerNodeDefinitions';
import useCytoscapeStyles from '@game/hooks/useCytoscapeStyles';
import { Land, Player } from '@game/state';

interface ActionableNode {
  type: 'land' | 'player';
  value: Land | Player;
}

interface CytoscapeProviderValue {
  cytoscapeContainerRef: (current: HTMLDivElement | null) => void;
  tooltipRef: MutableRefObject<HTMLDivElement | null>;
  clickedNode: ActionableNode | null;
  hoveredNode: ActionableNode | null;
  clearClickedNode: () => void;
  isTooltipHidden: boolean;
}

export const CytoscapeContext = createContext<CytoscapeProviderValue | null>(
  null,
);

function CytoscapeProvider({ children }: PropsWithChildren) {
  const { textColor } = useTheme();
  const cytoscapeStyles = useCytoscapeStyles();
  const landNodes = useCytoscapeLandNodeDefinitions();
  const landEdges = useCytoscapeLandEdgeDefinitions();
  const playerNodes = useCytoscapePlayerNodeDefinitions();
  const playerEdges = useCytoscapePlayerEdgeDefinitions();

  // State variables
  const [cy, setCy] = useState<Core | null>(null);
  const [clickedNode, setClickedNode] = useState<ActionableNode | null>(null);
  const [hoveredNode, setHoveredNode] = useState<ActionableNode | null>(null);
  const [isTooltipHidden, setTooltipHidden] = useState(true);

  // Setting up references to DOM elements
  const tooltipRef = useRef<HTMLDivElement | null>(null);
  const cytoscapeContainerRef = useCallback(
    (current: HTMLDivElement | null) => {
      if (!current) return;

      // Creating cytoscape instance
      const newCy = cytoscape({
        autolock: true,
        maxZoom: 1.5,
        minZoom: 0.75,
        layout: {
          name: 'preset',
        },
        style: cytoscapeStyles,
        container: current,
      });

      // Adding onclick hook for cytoscape
      newCy.on('click', 'node', (event: EventObjectNode) => {
        const { player, land } = event.target.data();

        // Setting clicked node
        setClickedNode({
          type: land ? 'land' : 'player',
          value: land || player,
        });

        // Hiding tooltip
        setTooltipHidden(true);
      });

      // Adding mousemove hook for tooltip
      newCy.on('mousemove', 'node', ({ target }: EventObjectNode) => {
        const { land, player } = target.data();

        // Setting hovered node
        setHoveredNode({
          type: land ? 'land' : 'player',
          value: land || player,
        });

        // Moving tooltip using popper js
        target.popper({
          content: () => tooltipRef.current as HTMLDivElement,
        });

        // Showing tooltip
        setTooltipHidden(false);
      });

      // Adding mouseout hook to remove tooltip
      newCy.on('mouseout', 'node', () => setTooltipHidden(true));

      // Setting up cytoscape instance's state
      setCy(newCy);
    },
    [cytoscapeStyles],
  );

  // Setting up popper js support for tooltips
  useEffect(() => {
    // Checking if popper factory is already registered
    if (!cy || !!cy.popperFactory) {
      return;
    }

    // Creating popper factory to move tooltip
    const popperFactory: PopperFactory = (ref, content, opts) => {
      const popperOptions = {
        // This allows the tooltip to show inside of the screen is going out of it!
        // Ref - https://floating-ui.com/docs/migration#configure-middleware
        middleware: [
          flip(),
          shift({ limiter: limitShift() }),
          offset(12),
          autoPlacement(),
        ],
        strategy: 'absolute' as Strategy,
        ...opts,
      };

      function update() {
        computePosition(ref, content, popperOptions).then(({ x, y }) => {
          // Adding positions to the tooltip element
          Object.assign(content.style, {
            left: `${x}px`,
            top: `${y}px`,
          });
        });
      }
      update();
      return { update };
    };

    // Registering popper factory
    cytoscape.use(cytoscapePopper(popperFactory));
  }, [cy]);

  // Updating nodes & edges
  useEffect(() => {
    if (!cy) return;

    // Clearing previous nodes and edges
    cy.remove(cy.elements());

    // Adding elements
    cy.add({
      nodes: [...landNodes, ...playerNodes],
      edges: [...landEdges, ...playerEdges],
    });

    // Adjusting zoom
    try {
      cy.fit(undefined, Number.MAX_VALUE);
    } catch {
      // Ignore
    }
  }, [cy, landEdges, landNodes, playerEdges, playerNodes]);

  // Adding player's turn animation
  useEffect(() => {
    if (!cy) return;

    // Creating callback for animation to replay alternatingly
    const callback = async (): Promise<any> => {
      // Adding animation to the turn player
      const node = cy.filter((ele) => ele.data('player.turn'));
      if (!node || !node.position()) {
        return new Promise((resolve) => {
          setTimeout(() => {
            resolve(callback);
          }, 1000);
        });
      }

      // Preparing blink animation for current player
      const animation = node.animation({
        duration: 1000,
        easing: 'ease',
        position: node.position(),
        renderedPosition: node.position(),
        style: {
          backgroundColor: textColor,
        },
      });

      return animation
        .play()
        .reverse()
        .play()
        .promise('complete')
        .then(callback);
    };
    callback();
  }, [cy, textColor]);

  // Creating provider's value
  const value: CytoscapeProviderValue = useMemo(
    () => ({
      cytoscapeContainerRef,
      tooltipRef,
      clickedNode,
      hoveredNode,
      clearClickedNode: () => setClickedNode(null),
      isTooltipHidden,
    }),
    [clickedNode, cytoscapeContainerRef, hoveredNode, isTooltipHidden],
  );

  return (
    <CytoscapeContext.Provider value={value}>
      {children}
    </CytoscapeContext.Provider>
  );
}

export default CytoscapeProvider;
