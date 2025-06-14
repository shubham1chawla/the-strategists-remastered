import { useEffect, useMemo } from 'react';
import { useDispatch } from 'react-redux';
import { DisconnectOutlined } from '@ant-design/icons';
import useActivities from '@activities/hooks/useActivities';
import { activityAdded, Activity, UpdateType } from '@activities/state';
import parseActivity from '@activities/utils/parseActivity';
import { advicesAddedOrPatched } from '@advices/state';
import {
  gameStateSetted,
  landsPatched,
  playerAdded,
  playerKicked,
  playersPatched,
} from '@game/state';
import syncGameStates from '@game/utils/syncGameStates';
import useLogin from '@login/hooks/useLogin';
import { loggedOut } from '@login/state';
import { predictionsAdded } from '@predictions/state';
import useNotifications from '@shared/hooks/useNotifications';
import { trendsAdded } from '@trends/state';

interface UpdatePayload {
  type: UpdateType;
  activity?: Activity;
  payload: any;
}

const UpdateInterceptor = () => {
  const { gameCode, playerId } = useLogin();
  const { subscribedTypes } = useActivities();
  const { contextHolder, ...api } = useNotifications();
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
        message.data,
      );
      switch (type) {
        case 'ADVICE':
          dispatch(advicesAddedOrPatched(payload));
          break;
        case 'BANKRUPTCY': {
          const { lands, players } = payload;
          dispatch(landsPatched(lands));
          dispatch(playersPatched(players));
          break;
        }
        case 'CLEAN_UP':
          dispatch(loggedOut());
          break;
        case 'CREATE':
          // Do nothing
          break;
        case 'INVEST': {
          const { land, players } = payload;
          dispatch(landsPatched([land]));
          dispatch(playersPatched(players));
          break;
        }
        case 'JOIN':
          dispatch(playerAdded(payload));
          break;
        case 'KICK':
          // Logging out if current player is kicked
          if (payload === playerId) {
            dispatch(loggedOut());
          }
          dispatch(playerKicked(payload));
          break;
        case 'MOVE':
          dispatch(playersPatched([payload]));
          break;
        case 'PING':
          // Do nothing
          break;
        case 'PREDICTION':
          dispatch(predictionsAdded(payload));
          break;
        case 'RENT':
          dispatch(playersPatched(payload));
          break;
        case 'RESET':
          setTimeout(() => syncGameStates(gameCode, dispatch));
          break;
        case 'SKIP':
          dispatch(playersPatched([payload]));
          break;
        case 'START':
          dispatch(playersPatched([payload]));
          dispatch(gameStateSetted('ACTIVE'));
          break;
        case 'TREND':
          dispatch(trendsAdded(payload));
          break;
        case 'TURN':
          dispatch(playersPatched(payload));
          break;
        case 'WIN':
          // Do nothing
          break;
        default:
          console.warn(`Unsupported update type: ${type}`);
      }
      if (!activity) return;
      dispatch(activityAdded(activity));
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

export default UpdateInterceptor;
