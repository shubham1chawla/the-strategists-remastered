import {
  createContext,
  PropsWithChildren,
  useCallback,
  useMemo,
  useState,
} from 'react';
import PortfolioModal from '@game/components/PortfolioModal';
import { Land, Player } from '@game/state';

interface PortfolioModelProps {
  perspective?: 'player' | 'land';
  node?: Player | Land;
  onCancel?: () => void;
}

interface PortfolioModalProviderValue extends PortfolioModelProps {
  setPortfolioModalProps: (props: PortfolioModelProps) => void;
}

export const PortfolioModalContext =
  createContext<PortfolioModalProviderValue | null>(null);

function PortfolioModalProvider({ children }: PropsWithChildren) {
  const [props, setProps] = useState<PortfolioModelProps>({});

  const onCancel = useCallback(() => {
    if (props.onCancel) {
      props.onCancel();
    }
    setProps({});
  }, [props]);

  const value: PortfolioModalProviderValue = useMemo(
    () => ({
      ...props,
      onCancel,
      setPortfolioModalProps: setProps,
    }),
    [props, onCancel],
  );

  return (
    <PortfolioModalContext.Provider value={value}>
      <PortfolioModal />
      {children}
    </PortfolioModalContext.Provider>
  );
}

export default PortfolioModalProvider;
