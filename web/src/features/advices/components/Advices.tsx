import { Flex } from 'antd';
import useAdvicesState from '@advices/hooks/useAdvicesState';
import AdviceCard from './AdviceCard';

function Advices() {
  const { playerAdvices } = useAdvicesState();
  return (
    <Flex className="strategists-advices" orientation="vertical" gap="large">
      {playerAdvices.map((advice) => (
        <AdviceCard key={advice.id} advice={advice} />
      ))}
    </Flex>
  );
}

export default Advices;
