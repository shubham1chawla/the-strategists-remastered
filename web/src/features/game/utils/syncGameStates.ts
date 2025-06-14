import { Dispatch } from 'react';
import { UnknownAction } from 'redux';
import { activitiesSetted, Activity } from '@activities/state';
import { Advice, advicesSetted } from '@advices/state';
import {
  gameStateSetted,
  Land,
  landsSetted,
  Player,
  playersCountConstraintsSetted,
  playersSetted,
} from '@game/state';
import { Prediction, predictionsSetted } from '@predictions/state';
import { Trend, trendsSetted } from '@trends/state';
import axios from 'axios';

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

export default syncGameStates;
