import { CSSProperties, useEffect } from 'react';
import { Col, Row } from 'antd';
import { AdminDashboard, Map, PlayerDashboard } from '.';
import { useDispatch, useSelector } from 'react-redux';
import { addPlayer, kickPlayer, Player } from '../redux';
import axios from 'axios';

export const Dashboard = () => {
  const { username, type } = useSelector((state: any) => state.user);
  const dispatch = useDispatch();

  useEffect(() => {
    // Updating players list
    axios.get('/api/players').then(async ({ data }) => {
      await data.forEach((player: Player) => {
        dispatch(addPlayer(player));
      });
    });

    // Setting up SSE for updates
    const updates = new EventSource(
      `${process.env.REACT_APP_API_BASE_URL}/api/updates/${username}`
    );
    updates.onmessage = (message: MessageEvent<any>) => {
      const { type, data } = JSON.parse(message.data);
      switch (type) {
        case 'JOIN':
          dispatch(addPlayer(data));
          break;
        case 'KICK':
          dispatch(kickPlayer(data));
          break;
        default:
          console.warn(`Unsupported update type: ${type}`);
      }
    };
    updates.onerror = console.error;
  }, [dispatch, username]);

  return (
    <Row style={dashboardContainer}>
      <Col style={mapStyle} flex="75%">
        <Map />
      </Col>
      <Col style={dashboardStyle} flex="25%">
        {type === 'admin' ? <AdminDashboard /> : <PlayerDashboard />}
      </Col>
    </Row>
  );
};

const mapStyle: CSSProperties = {
  backgroundColor: '#1b1c27',
};

const dashboardStyle: CSSProperties = {
  backgroundColor: '#7dbcea',
};

const dashboardContainer: CSSProperties = {
  height: '100vh',
};
