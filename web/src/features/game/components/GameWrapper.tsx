import { PropsWithChildren, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { Dispatch, UnknownAction } from 'redux';
import { useDispatch } from 'react-redux';
import { DisconnectOutlined } from '@ant-design/icons';
import axios from 'axios';
import useNotifications from '@shared/hooks/useNotifications';
import useActivitiesState from '@activities/hooks/useActivitiesState';
import {
  activityAdded,
  Activity,
  UpdateType,
  activitiesSetted,
} from '@activities/state';
import { Advice, advicesAddedOrPatched, advicesSetted } from '@advices/state';
import {
  Game,
  Land,
  Player,
  gameSetted,
  gamePatched,
  landsPatched,
  landsSetted,
  playerAdded,
  playerKicked,
  playersPatched,
  playersSetted,
} from '@game/state';
import useLoginState from '@login/hooks/useLoginState';
import { loggedOut } from '@login/state';
import {
  PlayerPrediction,
  playerPredictionsAdded,
  playerPredictionsSetted,
} from '@predictions/state';
import { Trend, trendsAdded, trendsSetted } from '@trends/state';

interface UpdatePayload {
  timestamp: number;
  gameCode: string | null;
  gameStep: number | null;
  type: UpdateType;
  activity: Activity | null;
  payload: any | null;
}

interface GameResponse {
  game: Game;
  players: Player[];
  lands: Land[];
  activities: Activity[];
  trends: Trend[];
  playerPredictions: PlayerPrediction[] | null;
  advices: Advice[] | null;
}

const syncUIByGameResponse = (
  gameResponse: GameResponse,
  dispatch: Dispatch<UnknownAction>,
) => {
  const {
    game,
    players,
    lands,
    activities,
    trends,
    playerPredictions,
    advices,
  } = gameResponse;
  [
    gameSetted(game),
    playersSetted(players),
    landsSetted(lands),
    activitiesSetted(activities),
    trendsSetted(trends),
    playerPredictionsSetted(playerPredictions || []),
    advicesSetted(advices || []),
  ].forEach(dispatch);
};

const syncUIByGameCode = async (
  gameCode: string,
  dispatch: Dispatch<UnknownAction>,
): Promise<void> => {
  const { data } = await axios.get<GameResponse>(`/api/games/${gameCode}`);
  syncUIByGameResponse(data, dispatch);
};

const alertUser = (event: BeforeUnloadEvent) => {
  event.preventDefault();
  return 'You are about to exit The Strategists! Do you want to continue?';
};

function GameWrapper({ children }: PropsWithChildren) {
  const { gameCode, playerId } = useLoginState();
  const { subscribedTypes } = useActivitiesState();
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
    syncUIByGameCode(gameCode, dispatch).catch(() => {
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
      const { timestamp, gameStep, type, payload, activity }: UpdatePayload =
        JSON.parse(message.data);
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
          dispatch(playerPredictionsAdded(payload));
          break;
        case 'RENT':
          dispatch(playersPatched(payload));
          break;
        case 'RESET':
          syncUIByGameResponse(payload, dispatch);
          break;
        case 'SKIP':
          dispatch(playersPatched([payload]));
          break;
        case 'START':
          dispatch(playersPatched([payload]));
          dispatch(gamePatched({ state: 'ACTIVE' }));
          break;
        case 'TREND':
          dispatch(trendsAdded(payload));
          break;
        case 'TURN':
          dispatch(playersPatched(payload));
          break;
        case 'WIN':
          // Assuming win payload's timestamp as game end time
          dispatch(gamePatched({ endAt: timestamp }));
          break;
        default:
          throw new Error(`Unsupported update type: ${type}`);
      }

      // Checking if activity part of payload
      if (!activity) return;
      dispatch(activityAdded(activity));
      if (subscribedTypes.includes(type)) {
        openNotification({ message: activity.text });
      }

      // Updating game's turn
      if (gameStep) {
        dispatch(gamePatched({ currentStep: gameStep }));
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
