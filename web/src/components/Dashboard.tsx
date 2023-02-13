import React from 'react';
import { Col, Row } from 'antd';
import Map from './Map';
import PlayerDashboard from './PlayerDashboard';
import AdminDashboard from './AdminDashboard';
import { useSelector } from 'react-redux';

const Dashboard = () => {
  const isAdmin = useSelector((state: any) => state.game.user === 'admin');
  return (
    <Row style={dashboardContainer}>
      <Col style={mapStyle} flex="75%">
        <Map />
      </Col>
      <Col style={dashboardStyle} flex="25%">
        {isAdmin ? <AdminDashboard /> : <PlayerDashboard />}
      </Col>
    </Row>
  );
};

const mapStyle: React.CSSProperties = {
  backgroundColor: '#3ba0e9',
};

const dashboardStyle: React.CSSProperties = {
  backgroundColor: '#7dbcea',
};

const dashboardContainer: React.CSSProperties = {
  height: '100vh',
};

export default Dashboard;
