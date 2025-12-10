import { useContext } from 'react';
import PortfolioModalProvider, {
  PortfolioModalContext,
} from '@game/providers/portfolioModalProvider';

const usePortfolioModal = () => {
  const value = useContext(PortfolioModalContext);
  if (!value) {
    throw new Error(
      `'${usePortfolioModal.name}' used outside '${PortfolioModalProvider.name}'!`,
    );
  }
  return value;
};

export default usePortfolioModal;
