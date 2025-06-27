import useCytoscape from '@game/hooks/useCytoscape';
import MapTooltip from './MapTooltip';
import PortfolioModal from './PortfolioModal';

function MapPanel() {
  const { cytoscapeContainerRef, clickedNode, clearClickedNode } =
    useCytoscape();
  return (
    <>
      <MapTooltip />
      <PortfolioModal
        open={!!clickedNode}
        onCancel={() => clearClickedNode()}
        perspective={clickedNode?.type}
        node={clickedNode?.value}
      />
      <div ref={cytoscapeContainerRef} className="strategists-map" />
    </>
  );
}

export default MapPanel;
