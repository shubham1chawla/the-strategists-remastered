import { MutableRefObject } from 'react';
import { Alert, Divider } from 'antd';
import { Land, Player } from '@game/state';
import PlayerStats from './PlayerStats';
import LandStats from './LandStats';

interface MapTooltipProps {
  player: Player | null;
  land: Land | null;
  tooltipRef: MutableRefObject<HTMLDivElement | null>;
  hidden: boolean;
}

const MapTooltip = (props: MapTooltipProps) => {
  const { player, land, tooltipRef, hidden } = props;
  return (
    <div
      ref={tooltipRef}
      role="tooltip"
      className={`strategists-map__tooltip ${
        hidden ? 'strategists-map__tooltip-hidden' : ''
      }`}
    >
      <>
        {player ? (
          <PlayerStats player={player} />
        ) : land ? (
          <LandStats land={land} />
        ) : null}
        <Divider>
          <Alert
            type="info"
            message={
              player
                ? `Click to check ${player.username}'s portfolio.`
                : land
                ? `Click to check ${land.name}'s investments`
                : null
            }
            banner
          />
        </Divider>
      </>
    </div>
  );
};

export default MapTooltip;
