import { Activity, Land, Player } from '../redux';

export const parseActivity = (activity: Activity): string => {
  const { type, val1, val2, val3, val4, val5 } = activity;
  switch (type) {
    case 'BANKRUPTCY':
      return `${val1} declared bankruptcy!`;
    case 'BONUS':
      return `${val1} gave ${val2} a bonus of ${val3} cash after completing one turn.`;
    case 'CHEAT':
      return `${val1} applied a cheat!`;
    case 'EVENT':
      return `${val1} caused ${val2} at ${val3} for ${val4} turns!`;
    case 'INVEST':
      return `${val1} invested in ${val2}% of ${val3}!`;
    case 'INVITE':
      return `${val1} invited to join The Strategists!`;
    case 'JOIN':
      return `${val1} accepted the invite!`;
    case 'KICK':
      return `${val1} kicked ${val2} out!`;
    case 'MOVE':
      return `${val1} travelled ${val2} steps and reached ${val3}.`;
    case 'PREDICTION':
      return val2 === 'BANKRUPT'
        ? `${val1} predicted to go bankrupt!`
        : `${val1} predicted to win!`;
    case 'RENT':
      return `${val1} paid ${val2} cash rent to ${val3} for ${val4}.`;
    case 'RESET':
      return `${val1} resetted The Strategists!`;
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
      console.error(activity);
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
      !!this.land.marketValue && this.ownership <= this.maxOfferableOwnership
    );
  }
}
