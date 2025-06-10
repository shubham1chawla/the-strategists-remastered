import { useSelector } from 'react-redux';
import { State } from '../store';

export const usePredictions = () => {
  return useSelector((state: State) => state.predictions);
};
