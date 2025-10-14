import { useSelector } from 'react-redux';
import { StrategistsState } from '@/store';

const usePredictionsState = () => {
  return useSelector((state: StrategistsState) => state.predictionsState);
};

export default usePredictionsState;
