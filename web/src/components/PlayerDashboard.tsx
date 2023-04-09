import { CSSProperties } from 'react';
import { Actions, Activity } from '.';

const ACTION_CONTAINER_HEIGHT = 100;

export const PlayerDashboard = () => {
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

const activityContainer: CSSProperties = {
  backgroundColor: '#191a24',
  height: `calc(100% - ${ACTION_CONTAINER_HEIGHT}px)`,
};

const actionContainer: CSSProperties = {
  backgroundColor: '#393c4f',
  marginTop: 'auto',
  height: `${ACTION_CONTAINER_HEIGHT}px`,
};

const playerDashboardContainer: CSSProperties = {
  height: '100%',
  display: 'flex',
  flexDirection: 'column',
};
