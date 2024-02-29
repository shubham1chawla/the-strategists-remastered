import { useMemo } from 'react';
import { useSelector } from 'react-redux';
import { State } from '../store';
import { LoginState } from './reducer';
import { Player } from '../lobby';

type Login = LoginState & { player: Player | undefined };

export const useLogin = (): Login => {
  const login = useSelector((state: State) => state.login);
  const { playerId } = login;
  const { players } = useSelector((state: State) => state.lobby);

  // Determining logged-in player
  const player = useMemo(
    () => players.find(({ id }) => id === playerId),
    [players, playerId]
  );

  return { ...login, player };
};
