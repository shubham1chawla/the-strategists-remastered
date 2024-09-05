package com.strategists.game.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.strategists.game.advice.AdviceType;
import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;

public interface AdviceRepository extends JpaRepository<Advice, Long> {

	List<Advice> findByGameOrderByIdDesc(Game game);

	Optional<Advice> findByPlayerAndType(Player player, AdviceType type);

	long deleteByGame(Game game);

}
