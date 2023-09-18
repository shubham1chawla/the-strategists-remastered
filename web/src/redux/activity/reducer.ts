import { ReactNode } from 'react';
import { ActivityActions } from '.';

export interface Activity {
  type:
    | 'BANKRUPTCY'
    | 'BONUS'
    | 'CHEAT'
    | 'END'
    | 'EVENT'
    | 'INVEST'
    | 'JAIL'
    | 'JOIN'
    | 'KICK'
    | 'MOVE'
    | 'RENT'
    | 'RESET'
    | 'START'
    | 'TRADE'
    | 'TURN';
  val1: string;
  val2: string | null;
  val3: string | null;
  val4: string | null;
  val5: string | null;
}

export const parseActivity = (activity: Activity): ReactNode => {
  const { type, val1, val2, val3, val4, val5 } = activity;
  switch (type) {
    case 'BANKRUPTCY':
      return `${val1} declared bankruptcy!`;
    case 'BONUS':
      return `${val1} gave ${val2} a bonus of ${val3} cash after completing one turn.`;
    case 'CHEAT':
      return `${val1} applied a cheat!`;
    case 'END':
      return `${val1} won The Strategists!`;
    case 'EVENT':
      return `${val1} caused ${val2} at ${val3} for ${val4} turns!`;
    case 'INVEST':
      return `${val1} invested in ${val2}% of ${val3}`;
    case 'JAIL':
      return `${val1} just got arrested!`;
    case 'JOIN':
      return `${val1} joined the game with ${val2} cash.`;
    case 'KICK':
      return `${val1} kicked ${val2} out!`;
    case 'MOVE':
      return `${val1} travelled ${val2} steps and reached ${val3}.`;
    case 'RENT':
      return `${val1} paid ${val2} cash rent to ${val3} for ${val4}.`;
    case 'RESET':
      return `${val1} resetted The Strategists!`;
    case 'START':
      return `${val1} started The Strategists! ${val2}'s turn to invest.`;
    case 'TRADE':
      return `${val1} traded ${val2}% of ${val3} with ${val4} for ${val5} cash.`;
    case 'TURN':
      return `${val1} passed turn to ${val2}.`;
    default:
      console.error(activity);
      return `Unknwon activity type: ${type}`;
  }
};

export type ActivityState = Activity[];

export const activityReducer = (
  state: ActivityState = [],
  action: any
): ActivityState => {
  const { type, payload } = action;
  switch (type) {
    case ActivityActions.Types.SET_ACTIVITIES:
      return [...payload];

    case ActivityActions.Types.ADD_ACTIVITY:
      return [payload, ...state];

    default:
      return state;
  }
};
