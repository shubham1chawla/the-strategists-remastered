import axios from 'axios';
import { useEffect, useState } from 'react';

const Map = () => {
  const [lands, setLands] = useState<any[]>([]);

  useEffect(() => {
    axios.get('/api/lands').then((res) => {
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
