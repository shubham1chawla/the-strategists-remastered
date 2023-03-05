import React, { useEffect, useState } from 'react';
import axios from 'axios';

const Map = () => {
  const [lands, setLands] = useState<any[]>([]);

  useEffect(() => {
    axios.get('http://localhost:8090/api/lands').then((res) => {
      setLands(res.data);
    });
  }, []);

  return (
    <div>
      <h2>This is Map Component</h2>
      {lands.map((land, i) => (
        <h2 key={i}>{land.name}</h2>
      ))}
    </div>
  );
};

export default Map;
