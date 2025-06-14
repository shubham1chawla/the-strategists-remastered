import { useSelector } from 'react-redux';
import { State } from '@/store';

const usePredictions = () => {
  return useSelector((state: State) => state.predictions);
};

export default usePredictions;
