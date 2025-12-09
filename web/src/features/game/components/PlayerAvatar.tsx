import { Avatar } from 'antd';
import useTheme from '@shared/hooks/useTheme';

interface PlayerAvatarProps {
  username: string;
}

function PlayerAvatar({ username }: PlayerAvatarProps) {
  const { getPlayerAvatarDataUri } = useTheme();
  return (
    <Avatar
      src={
        <img
          draggable={false}
          src={getPlayerAvatarDataUri(username)}
          alt="avatar"
        />
      }
      shape="square"
      size="small"
    />
  );
}

export default PlayerAvatar;
