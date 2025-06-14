import { useContext } from 'react';
import { ThemeContext } from '@shared/providers/themeProvider';

const useTheme = () => {
  const value = useContext(ThemeContext);
  if (!value) {
    throw new Error(`useTheme hook should only be used inside ThemeProvider!`);
  }
  return value;
};

export default useTheme;
