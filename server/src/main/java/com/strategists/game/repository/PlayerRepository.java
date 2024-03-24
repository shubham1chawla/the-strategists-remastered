package com.strategists.game.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.Player.State;

public interface PlayerRepository extends JpaRepository<Player, Long> {

	boolean existsByGameAndUsername(Game game, String username);

	Optional<Player> findByGameAndUsername(Game game, String username);

	boolean existsByGameAndHost(Game game, boolean host);

	boolean existsByEmail(String email);

	Optional<Player> findByEmail(String email);

	boolean existsByGameAndTurn(Game game, boolean turn);

	Optional<Player> findByGameAndTurn(Game game, boolean turn);

	long countByGame(Game game);

	List<Player> findByGame(Game game);

	long countByGameAndState(Game game, State state);

	List<Player> findByGameAndState(Game game, State state);

	List<Player> findByGameOrderByBankruptcyOrder(Game game);

}
