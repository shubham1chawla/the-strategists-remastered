import { useMemo } from 'react';
import { Dark } from '@antv/g2';
import useTheme from './useTheme';

const useChartTheme = () => {
  const theme = useTheme();

  const chartTheme = useMemo(
    () => ({
      ...Dark(),
      axisLeft: {
        labelFill: theme.textColor,
        labelOpacity: 1,
      },
      axisBottom: {
        labelFill: theme.textColor,
        labelOpacity: 1,
      },
      color: theme.accentColor,
      view: {
        viewFill: 'transparent',
      },
    }),
    [theme],
  );

  return chartTheme;
};

export default useChartTheme;
