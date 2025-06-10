import { useMemo } from 'react';
import { useSelector } from 'react-redux';
import { State } from '../store';
import { Player } from '../features/game/slice';
import { LoginState } from '../features/login/slice';

type Login = LoginState & { player: Player | undefined };

export const useLogin = (): Login => {
  const login = useSelector((state: State) => state.login);
  const { playerId } = login;
  const { players } = useSelector((state: State) => state.game);

  // Determining logged-in player
  const player = useMemo(
    () => players.find(({ id }) => id === playerId),
    [players, playerId]
  );

  return { ...login, player };
};
