import useCytoscape from '@game/hooks/useCytoscape';
import { Land, Player } from '@game/state';
import LandCard from './LandCard';
import PlayerCard from './PlayerCard';

function MapTooltip() {
  const { tooltipRef, hoveredNode, isTooltipHidden } = useCytoscape();
  const player: Player | null =
    hoveredNode?.type === 'player' ? (hoveredNode.value as Player) : null;
  const land: Land | null =
    hoveredNode?.type === 'land' ? (hoveredNode.value as Land) : null;
  return (
    <div
      ref={tooltipRef}
      role="tooltip"
      className={`strategists-map__tooltip ${
        isTooltipHidden ? 'strategists-map__tooltip-hidden' : ''
      }`}
    >
      {player && <PlayerCard player={player} highlight />}
      {land && <LandCard land={land} highlight />}
    </div>
  );
}

export default MapTooltip;
