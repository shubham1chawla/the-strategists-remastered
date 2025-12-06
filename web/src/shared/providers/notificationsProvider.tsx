import {
  createContext,
  PropsWithChildren,
  useCallback,
  useEffect,
  useMemo,
  useRef,
} from 'react';
import { notification } from 'antd';
import { ArgsProps } from 'antd/es/notification';
import { NotificationInstance } from 'antd/es/notification/interface';

const NOTIFICATIONS_STACK = false;
const NOTIFICATION_MAX_COUNT = 5;
const NOTIFICATION_DURATION = 6; // Seconds

interface NotificationsContextValue {
  instance: NotificationInstance;
  openNotification: (args: ArgsProps) => void;
  infoNotification: (args: ArgsProps) => void;
  errorNotification: (args: ArgsProps) => void;
}

export const NotificationsContext =
  createContext<NotificationsContextValue | null>(null);

function NotificationsProvider({ children }: PropsWithChildren) {
  const [instance, contextHolder] = notification.useNotification({
    stack: NOTIFICATIONS_STACK,
    maxCount: NOTIFICATION_MAX_COUNT,
  });

  /**
   * Variable `instance` keeps changing after antd@6 upgrade.
   * Wrapping it in a `useRef` to prevent re-rendering of
   * GameWrapper and entering infinite loop of calling API.
   */
  const instanceRef = useRef(instance);
  useEffect(() => {
    instanceRef.current = instance;
  }, [instance]);

  const openNotification = useCallback(
    (args: ArgsProps) => {
      instanceRef.current.open({
        ...args,
        duration: args.duration || NOTIFICATION_DURATION,
      });
    },
    [instanceRef],
  );

  const infoNotification = useCallback(
    (args: ArgsProps) => {
      instanceRef.current.info({
        ...args,
        duration: args.duration || NOTIFICATION_DURATION,
      });
    },
    [instanceRef],
  );

  const errorNotification = useCallback(
    (args: ArgsProps) => {
      instanceRef.current.error({
        ...args,
        duration: args.duration || NOTIFICATION_DURATION,
      });
    },
    [instanceRef],
  );

  const value: NotificationsContextValue = useMemo(
    () => ({
      instance: instanceRef.current,
      openNotification,
      infoNotification,
      errorNotification,
    }),
    [instanceRef, openNotification, infoNotification, errorNotification],
  );

  return (
    <NotificationsContext.Provider value={value}>
      {contextHolder}
      {children}
    </NotificationsContext.Provider>
  );
}

export default NotificationsProvider;
