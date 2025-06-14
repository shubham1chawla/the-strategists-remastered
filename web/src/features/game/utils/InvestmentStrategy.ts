import { Land, Player } from '@game/state';

class InvestmentStrategy {
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

export default InvestmentStrategy;
