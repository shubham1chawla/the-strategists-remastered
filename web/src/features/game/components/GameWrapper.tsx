import { PropsWithChildren, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { Dispatch, UnknownAction } from 'redux';
import { useDispatch } from 'react-redux';
import { DisconnectOutlined } from '@ant-design/icons';
import axios from 'axios';
import useNotifications from '@shared/hooks/useNotifications';
import useActivities from '@activities/hooks/useActivities';
import {
  activityAdded,
  Activity,
  UpdateType,
  activitiesSetted,
} from '@activities/state';
import parseActivity from '@activities/utils/parseActivity';
import { Advice, advicesAddedOrPatched, advicesSetted } from '@advices/state';
import {
  gameStateSetted,
  Land,
  landsPatched,
  landsSetted,
  Player,
  playerAdded,
  playerKicked,
  playersCountConstraintsSetted,
  playersPatched,
  playersSetted,
} from '@game/state';
import useLogin from '@login/hooks/useLogin';
import { loggedOut } from '@login/state';
import {
  Prediction,
  predictionsAdded,
  predictionsSetted,
} from '@predictions/state';
import { Trend, trendsAdded, trendsSetted } from '@trends/state';

interface UpdatePayload {
  type: UpdateType;
  activity?: Activity;
  payload: any;
}

interface GameResponse {
  state: 'LOBBY' | 'ACTIVE';
  minPlayersCount: number;
  maxPlayersCount: number;
  players: Player[];
  lands: Land[];
  activities: Activity[];
  trends: Trend[];
  predictions: Prediction[] | null;
  advices: Advice[] | null;
}

const syncGameStates = async (
  gameCode: string,
  dispatch: Dispatch<UnknownAction>,
): Promise<void> => {
  const { data } = await axios.get<GameResponse>(`/api/games/${gameCode}`);
  const {
    state,
    minPlayersCount,
    maxPlayersCount,
    players,
    lands,
    activities,
    trends,
    predictions,
    advices,
  } = data;
  [
    gameStateSetted(state),
    playersCountConstraintsSetted([minPlayersCount, maxPlayersCount]),
    playersSetted(players),
    landsSetted(lands),
    activitiesSetted(activities),
    trendsSetted(trends),
    predictionsSetted(predictions || []),
    advicesSetted(advices || []),
  ].forEach(dispatch);
};

const alertUser = (event: BeforeUnloadEvent) => {
  event.preventDefault();
  return 'You are about to exit The Strategists! Do you want to continue?';
};

function GameWrapper({ children }: PropsWithChildren) {
  const { gameCode, playerId } = useLogin();
  const { subscribedTypes } = useActivities();
  const { openNotification, errorNotification } = useNotifications();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  // Checking if player is logged-in
  useEffect(() => {
    if (!gameCode) {
      navigate('/login');
      return undefined;
    }

    // Syncing game's state
    syncGameStates(gameCode, dispatch).catch(() => {
      errorNotification({
        message: 'Something went wrong!',
        description:
          'Please try logging in again. If the problem persists, please contact the developers.',
      });
      dispatch(loggedOut());
    });

    // Dashboard component's unmount event
    window.addEventListener('beforeunload', alertUser);
    return () => {
      // Removing listener if user logouts
      window.removeEventListener('beforeunload', alertUser);
    };
  }, [dispatch, navigate, gameCode, errorNotification]);

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
    updates.onerror = () => {
      // Preventing reconnection using the same instance.
      updates.close();

      // Showing notification to the user, urging them to refresh the page.
      errorNotification({
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
          throw new Error(`Unsupported update type: ${type}`);
      }
      if (!activity) return;
      dispatch(activityAdded(activity));
      if (subscribedTypes.includes(type)) {
        openNotification({ message: parseActivity(activity) });
      }
    };
  }, [
    openNotification,
    errorNotification,
    dispatch,
    subscribedTypes,
    updates,
    gameCode,
    playerId,
  ]);

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

  return children;
}

export default GameWrapper;
