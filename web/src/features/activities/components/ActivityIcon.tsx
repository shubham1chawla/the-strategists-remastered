import {
  AppstoreAddOutlined,
  BulbOutlined,
  CheckOutlined,
  ClockCircleOutlined,
  CrownOutlined,
  DoubleRightOutlined,
  FallOutlined,
  FireOutlined,
  RiseOutlined,
  StopOutlined,
  UserAddOutlined,
  UserDeleteOutlined,
} from '@ant-design/icons';
import BankruptcyIcon from '@shared/components/BankruptcyIcon';
import { UpdateType } from '@activities/state';

interface ActivityIconProps {
  type: UpdateType;
}

function ActivityIcon({ type }: ActivityIconProps) {
  switch (type) {
    case 'BANKRUPTCY':
      return <BankruptcyIcon />;
    case 'CREATE':
      return <AppstoreAddOutlined />;
    case 'INVEST':
      return <RiseOutlined />;
    case 'JOIN':
      return <UserAddOutlined />;
    case 'KICK':
      return <UserDeleteOutlined />;
    case 'MOVE':
      return <DoubleRightOutlined />;
    case 'PREDICTION':
      return <BulbOutlined />;
    case 'RENT':
      return <FallOutlined />;
    case 'RESET':
      return <StopOutlined />;
    case 'SKIP':
      return <ClockCircleOutlined />;
    case 'START':
      return <FireOutlined />;
    case 'TURN':
      return <CheckOutlined />;
    case 'WIN':
      return <CrownOutlined />;
    default:
      return null;
  }
}

export default ActivityIcon;
