package com.strategists.game.repository;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByGameOrderByIdDesc(Game game);

    void deleteByGame(Game game);

}
