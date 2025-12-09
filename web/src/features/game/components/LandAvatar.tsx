import { Avatar } from 'antd';
import useTheme from '@shared/hooks/useTheme';

interface LandAvatarProps {
  name: string;
}

function LandAvatar({ name }: LandAvatarProps) {
  const { getLandAvatarDataUri } = useTheme();
  return (
    <Avatar
      src={
        <img draggable={false} src={getLandAvatarDataUri(name)} alt="avatar" />
      }
      shape="square"
      size="small"
    />
  );
}

export default LandAvatar;
