package com.strategists.game.repository;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Land;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LandRepository extends JpaRepository<Land, Long> {

    List<Land> findByGameOrderById(Game game);

    long countByGame(Game game);

}
