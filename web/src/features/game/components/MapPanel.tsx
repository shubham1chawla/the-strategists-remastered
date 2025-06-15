import useCytoscape from '@game/hooks/useCytoscape';
import PortfolioModal from './PortfolioModal';
import MapTooltip from './MapTooltip';

const MapPanel = () => {
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
      <div ref={cytoscapeContainerRef} className="strategists-map"></div>
    </>
  );
};

export default MapPanel;
