import React from 'react';
import Activity from './Activity';
import Actions from './Actions';

const actionContainerHeight = 100;

const PlayerDashboard = () => {
  return (
    <div style={playerDashboardContainer}>
      <div style={activityContainer}>
        <Activity />
      </div>
      <div style={actionContainer}>
        <Actions />
      </div>
    </div>
  );
};

const activityContainer: React.CSSProperties = {
  backgroundColor: '#191a24',
  height: `calc(100% - ${actionContainerHeight}px)`,
};

const actionContainer: React.CSSProperties = {
  backgroundColor: '#393c4f',
  marginTop: 'auto',
  height: `${actionContainerHeight}px`,
};

const playerDashboardContainer: React.CSSProperties = {
  height: '100%',
  display: 'flex',
  flexDirection: 'column',
};

export default PlayerDashboard;
