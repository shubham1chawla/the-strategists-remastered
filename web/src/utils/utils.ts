import { Dispatch } from 'react';
import { AnyAction } from 'redux';
import { activitiesSetted, Activity } from '../features/activities/slice';
import { Advice, advicesSetted } from '../features/advices/slice';
import {
  Player,
  Land,
  gameStateSetted,
  playersCountConstraintsSetted,
  playersSetted,
  landsSetted,
} from '../features/game/slice';
import { Prediction, predictionsSetted } from '../features/predictions/slice';
import { Trend, trendsSetted } from '../features/trends/slice';
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

export const syncGameStates = async (
  gameCode: string,
  dispatch: Dispatch<AnyAction>
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

export const parseActivity = (activity: Activity): string => {
  const { type, val1, val2, val3, val4, val5 } = activity;
  switch (type) {
    case 'BANKRUPTCY':
      return `${val1} declared bankruptcy!`;
    case 'BONUS':
      return `${val1} gave ${val2} a bonus of ${val3} cash after completing one turn.`;
    case 'CHEAT':
      return `${val1} applied a cheat!`;
    case 'CREATE':
      return `${val1} created game ${val2}`;
    case 'EVENT':
      return `${val1} caused ${val2} at ${val3} for ${val4} turns!`;
    case 'INVEST':
      return `${val1} invested in ${val2}% of ${val3}!`;
    case 'JOIN':
      return `${val1} joined The Strategists!`;
    case 'KICK':
      return `Host kicked ${val1} out!`;
    case 'MOVE':
      return `${val1} travelled ${val2} steps and reached ${val3}.`;
    case 'PREDICTION':
      return val2 === 'WINNER'
        ? `${val1} is likely to win based on the predictions!`
        : `${val1} leads slightly based on the predictions.`;
    case 'RENT':
      return `${val1} paid ${val2} cash rent to ${val3} for ${val4}.`;
    case 'RESET':
      return `Host resetted The Strategists!`;
    case 'SKIP':
      return `${val1}'s turn skipped due to inactivity!`;
    case 'START':
      return `The Strategists started! ${val1}'s turn to invest.`;
    case 'TRADE':
      return `${val1} traded ${val2}% of ${val3} with ${val4} for ${val5} cash.`;
    case 'TURN':
      return `${val1} passed turn to ${val2}.`;
    case 'WIN':
      return `${val1} won The Strategists!`;
    default:
      console.warn(activity);
      return `Unknwon activity type: ${type}`;
  }
};

export class InvestmentStrategy {
  static readonly RESERVED_CASH = 1;

  constructor(
    readonly player: Player,
    readonly land: Land,
    public ownership: number = 0
  ) {}

  get cost() {
    const ownership = Math.min(this.ownership, this.maxOfferableOwnership);
    return (ownership * this.land.marketValue) / 100;
  }

  get availableOwnership() {
    return 100 - this.land.totalOwnership;
  }

  get maxOfferableOwnership() {
    const cash = this.player.cash - InvestmentStrategy.RESERVED_CASH;
    return Math.min(
      this.availableOwnership,
      Math.floor((cash * 100) / this.land.marketValue)
    );
  }

  get feasible() {
    return (
      !!this.land.marketValue &&
      this.ownership > 0 &&
      this.ownership <= this.maxOfferableOwnership
    );
  }
}
