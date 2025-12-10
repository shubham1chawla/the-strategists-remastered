import useCytoscape from '@game/hooks/useCytoscape';
import MapTooltip from './MapTooltip';

function MapPanel() {
  const { cytoscapeContainerRef } = useCytoscape();
  return (
    <>
      <MapTooltip />
      <div ref={cytoscapeContainerRef} className="strategists-map" />
    </>
  );
}

export default MapPanel;
