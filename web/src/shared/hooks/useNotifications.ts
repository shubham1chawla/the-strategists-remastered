import { notification } from 'antd';
import { ArgsProps as NotificationArgsProps } from 'antd/es/notification';

const NOTIFICATION_DURATION = 6; // Seconds

const useNotifications = () => {
  const [instance, contextHolder] = notification.useNotification({
    stack: false,
    maxCount: 5,
  });
  return {
    instance,
    contextHolder,
    open: (args: NotificationArgsProps) => {
      instance.open({
        ...args,
        duration: args.duration || NOTIFICATION_DURATION,
      });
    },
    info: (args: NotificationArgsProps) => {
      instance.info({
        ...args,
        duration: args.duration || NOTIFICATION_DURATION,
      });
    },
    error: (args: NotificationArgsProps) => {
      instance.error({
        ...args,
        duration: args.duration || NOTIFICATION_DURATION,
      });
    },
  };
};

export default useNotifications;
