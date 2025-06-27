import { Alert, Divider } from 'antd';
import useCytoscape from '@game/hooks/useCytoscape';
import { Land, Player } from '@game/state';
import LandStats from './LandStats';
import PlayerStats from './PlayerStats';

function MapTooltip() {
  const { tooltipRef, hoveredNode, isTooltipHidden } = useCytoscape();
  return (
    <div
      ref={tooltipRef}
      role="tooltip"
      className={`strategists-map__tooltip ${
        isTooltipHidden ? 'strategists-map__tooltip-hidden' : ''
      }`}
    >
      {hoveredNode?.type === 'player' ? (
        <PlayerStats player={hoveredNode.value as Player} />
      ) : hoveredNode?.type === 'land' ? (
        <LandStats land={hoveredNode.value as Land} />
      ) : null}
      <Divider>
        <Alert
          type="info"
          message={
            hoveredNode?.type === 'player'
              ? `Click to check ${(hoveredNode.value as Player).username}'s portfolio.`
              : hoveredNode?.type === 'land'
                ? `Click to check ${(hoveredNode.value as Land).name}'s investments`
                : null
          }
          banner
        />
      </Divider>
    </div>
  );
}

export default MapTooltip;
