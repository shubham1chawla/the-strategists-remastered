import React from "react";

type DashboardProps = {
  user: string;
};

const Dashboard = ({ user }: DashboardProps) => {
  return (
    <div>
        <h1>Hello <span className="text-red-700">{user}</span>! This is Dashboard Component</h1>
    </div>
  );
};

export default Dashboard;
