package com.strategists.game.repository;

import com.strategists.game.advice.AdviceType;
import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdviceRepository extends JpaRepository<Advice, Long> {

    List<Advice> findByGameOrderByPriority(Game game);

    Optional<Advice> findByPlayerAndType(Player player, AdviceType type);

    List<Advice> findByPlayerAndViewed(Player player, boolean viewed);

    void deleteByGame(Game game);

}
