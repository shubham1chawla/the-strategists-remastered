import React from 'react';
import Feed from './Feed';
import Actions from './Actions';

const PlayerDashboard = () => {
  return (
    <div style={playerDashboardContainer}>
      <div style={feedContainer}>
        <Feed />
      </div>
      <div style={actionContainer}>
        <Actions />
      </div>
    </div>
  );
};

const feedContainer: React.CSSProperties = {
  backgroundColor: '#3ba0bc',
  flexGrow: 1,
};

const actionContainer: React.CSSProperties = {
  backgroundColor: '#7dbcbc',
  marginTop: 'auto',
  height: '100px',
};

const playerDashboardContainer: React.CSSProperties = {
  height: '100vh',
  display: 'flex',
  flexDirection: 'column',
};

export default PlayerDashboard;
