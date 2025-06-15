import NotificationsProvider, {
  NotificationsContext,
} from '@shared/providers/notificationsProvider';
import { useContext } from 'react';

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
