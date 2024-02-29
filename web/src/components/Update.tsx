import { useEffect, useMemo } from 'react';
import { useDispatch } from 'react-redux';
import { notification } from 'antd';
import { DisconnectOutlined } from '@ant-design/icons';
import {
  Activity,
  ActivityActions,
  LobbyActions,
  LoginActions,
  TrendActions,
  UpdateType,
  useActivities,
  useLogin,
} from '../redux';
import { parseActivity, syncGameStates } from '../utils';

/**
 * -----  UPDATE COMPONENT BELOW  -----
 */

interface UpdatePayload {
  type: UpdateType;
  activity?: Activity;
  payload: any;
}

export const Update = () => {
  const { gameCode, playerId } = useLogin();
  const { subscribedTypes } = useActivities();
  const [api, contextHolder] = notification.useNotification();
  const dispatch = useDispatch();

  /**
   * This useMemo ensures that we'll change the event source's instance
   * only when the username and game code changes.
   */
  const updates = useMemo(() => {
    return !gameCode || !playerId
      ? null
      : new EventSource(`/api/games/${gameCode}/sse?playerId=${playerId}`);
  }, [gameCode, playerId]);

  /**
   * This useEffect will only update the event source's onmessage hook.
   */
  useEffect(() => {
    if (!updates || !gameCode) return;

    // Setting up onerror startegy for the event source
    updates.onerror = (error) => {
      console.error(error);

      // Preventing reconnection using the same instance.
      updates.close();

      // Showing notification to the user, urging them to refresh the page.
      api.error({
        icon: <DisconnectOutlined />,
        message: 'Disconnected!',
        description:
          'We lost the connection to our servers. Refresh the page to reconnect!',
        duration: 0,
        onClose: () => window.location.reload(),
      });
    };

    // Setting up on message strategy for the event source
    updates.onmessage = (message: MessageEvent<any>) => {
      const { type, payload, activity }: UpdatePayload = JSON.parse(
        message.data
      );
      switch (type) {
        case 'BANKRUPTCY': {
          const { lands, players } = payload;
          dispatch(LobbyActions.patchLands(lands));
          dispatch(LobbyActions.patchPlayers(players));
          break;
        }
        case 'INVEST': {
          const { land, players } = payload;
          dispatch(LobbyActions.patchLands([land]));
          dispatch(LobbyActions.patchPlayers(players));
          break;
        }
        case 'JOIN':
          dispatch(LobbyActions.addPlayer(payload));
          break;
        case 'KICK':
          // Logging out if current player is kicked
          if (payload === playerId) {
            dispatch(LoginActions.logout());
          }
          dispatch(LobbyActions.kickPlayer(payload));
          break;
        case 'MOVE':
          dispatch(LobbyActions.patchPlayers([payload]));
          break;
        case 'PING':
        case 'PREDICTION':
          // Do nothing
          break;
        case 'RENT':
          dispatch(LobbyActions.patchPlayers(payload));
          break;
        case 'RESET':
          /**
           * Unknown issue here. Some clients refresh game's state but some don't (rarely).
           * Adding the setTimeout seems to work here but root cause is still unknown.
           */
          setTimeout(() => syncGameStates(gameCode, dispatch));
          break;
        case 'START':
          dispatch(LobbyActions.patchPlayers([payload]));
          dispatch(LobbyActions.setState('ACTIVE'));
          break;
        case 'TREND':
          dispatch(TrendActions.addTrends(payload));
          break;
        case 'TURN':
          dispatch(LobbyActions.patchPlayers(payload));
          break;
        case 'WIN':
          // Do nothing
          break;
        default:
          console.warn(`Unsupported update type: ${type}`);
      }
      if (!activity) return;
      dispatch(ActivityActions.addActivity(activity));
      if (subscribedTypes.includes(type)) {
        api.open({ message: parseActivity(activity) });
      }
    };
  }, [api, dispatch, subscribedTypes, updates, gameCode, playerId]);

  /**
   * This useEffect will close the event source for the
   * current user if they decide to logout or closes the tab.
   */
  useEffect(() => {
    return () => {
      if (!updates) {
        return;
      }
      updates.onmessage = null;
      updates.onerror = null;
      updates.close();
    };
  }, [updates]);

  return <>{contextHolder}</>;
};
