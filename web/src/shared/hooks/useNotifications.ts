import { useContext } from 'react';
import NotificationsProvider, {
  NotificationsContext,
} from '@shared/providers/notificationsProvider';

const useNotifications = () => {
  const value = useContext(NotificationsContext);
  if (!value) {
    throw new Error(
      `'${useNotifications.name}' used outside '${NotificationsProvider.name}'!`,
    );
  }
  return value;
};

export default useNotifications;
