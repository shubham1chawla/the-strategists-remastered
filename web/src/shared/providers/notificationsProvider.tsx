import { createContext, PropsWithChildren, useMemo } from 'react';
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

  const value: NotificationsContextValue = useMemo(
    () => ({
      instance,
      openNotification: (args: ArgsProps) => {
        instance.open({
          ...args,
          duration: args.duration || NOTIFICATION_DURATION,
        });
      },
      infoNotification: (args: ArgsProps) => {
        instance.info({
          ...args,
          duration: args.duration || NOTIFICATION_DURATION,
        });
      },
      errorNotification: (args: ArgsProps) => {
        instance.error({
          ...args,
          duration: args.duration || NOTIFICATION_DURATION,
        });
      },
    }),
    [instance],
  );

  return (
    <NotificationsContext.Provider value={value}>
      {contextHolder}
      {children}
    </NotificationsContext.Provider>
  );
}

export default NotificationsProvider;
