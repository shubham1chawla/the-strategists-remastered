import { useMemo } from 'react';
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

  const className = useMemo(() => {
    const classes = ['strategists-map-tooltip'];
    if (isTooltipHidden) {
      classes.push('strategists-map-tooltip-hidden');
    }
    return classes.join(' ');
  }, [isTooltipHidden]);
  return (
    <div ref={tooltipRef} role="tooltip" className={className}>
      {player && <PlayerCard player={player} />}
      {land && <LandCard land={land} />}
    </div>
  );
}

export default MapTooltip;
