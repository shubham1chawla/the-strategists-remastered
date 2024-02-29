import { useSelector } from 'react-redux';
import { State } from '../store';

export const useTrends = () => {
  return useSelector((state: State) => state.trend);
};
