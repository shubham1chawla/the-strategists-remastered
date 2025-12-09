import { useMemo } from 'react';
import { StylesheetCSS } from 'cytoscape';
import useTheme from '@shared/hooks/useTheme';

const useCytoscapeStyles = () => {
  const theme = useTheme();

  const styles: StylesheetCSS[] = useMemo(
    () => [
      {
        selector: 'node',
        css: {
          width: 20,
          height: 20,
          color: theme.textColor,
          'text-margin-y': -10,
          'text-halign': 'center',
          'text-valign': 'top',
          'text-background-color': theme.darkColor,
          'text-background-opacity': 1,
          'text-background-padding': '4px',
        },
      },
      {
        selector: 'edge',
        css: {
          'target-arrow-shape': 'triangle',
        },
      },
      {
        selector: '.land',
        css: {
          label: 'data(name)',
          shape: 'ellipse',
          'pie-size': '100%',
          ...theme.playerColors.reduce(
            (pieStyles, color, i) =>
              Object.assign(pieStyles, {
                [`pie-${i + 1}-background-color`]: color,
                [`pie-${i + 1}-background-size`]: `mapData(investments.${i}, 0, 100, 0, 100)`,
              }),
            {},
          ),
        },
      },
      {
        selector: '.land-invested',
        css: {
          width: 30,
          height: 30,
        },
      },
      {
        selector: '.prison',
        css: {
          label: 'data(name)',
          shape: 'pentagon',
          backgroundColor: theme.accentColor,
        },
      },
      {
        selector: '.ungrouped-player',
        css: {
          label: 'data(name)',
          shape: 'round-rectangle',
          color: 'data(color)',
          'corner-radius': 2,
          'background-image': 'data(avatarDataUri)',
          'background-fit': 'cover',
          'font-weight': 'bold',
        },
      },
      {
        selector: '.turn-player',
        css: {
          label: 'data(name)',
          color: 'data(color)',
          'font-weight': 'bold',
        },
      },
      // ungrouped-player (with label) = turn-player (with label) + grouped-player (without label)
      {
        selector: '.grouped-player',
        css: {
          shape: 'round-rectangle',
          'corner-radius': 2,
          'background-image': 'data(avatarDataUri)',
          'background-fit': 'cover',
        },
      },
      {
        selector: '.land-edge',
        css: {
          width: 2,
          'curve-style': 'bezier',
        },
      },
      {
        selector: '.player-edge',
        css: {
          width: 2,
          'curve-style': 'bezier',
        },
      },
      {
        selector: ':parent',
        css: {
          'background-opacity': 0,
          'border-opacity': 0,
        },
      },
    ],
    [theme],
  );

  return styles;
};

export default useCytoscapeStyles;
