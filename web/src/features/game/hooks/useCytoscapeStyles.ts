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
        css: {
          width: 20,
          height: 20,
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
          shape: 'pentagon',
          backgroundColor: theme.accentColor,
        },
      },
      {
        selector: '.player',
        css: {
          shape: 'triangle',
          color: 'data(color)',
          backgroundColor: 'data(color)',
          'font-weight': 'bold',
        },
      },
      {
        selector: '.land-edge',
        css: {
          width: 2,
          'curve-style': 'bezier',
          'target-arrow-shape': 'triangle',
        },
      },
      {
        selector: '.player-edge',
        css: {
          width: 2,
          'curve-style': 'unbundled-bezier',
          'target-arrow-shape': 'triangle',
          'line-style': 'dashed',
        },
      },
    ],
    [theme],
  );

  return styles;
};

export default useCytoscapeStyles;
