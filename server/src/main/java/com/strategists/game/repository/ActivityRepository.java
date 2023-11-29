package com.strategists.game.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.strategists.game.entity.Activity;
import com.strategists.game.update.UpdateType;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

	List<Activity> findByOrderByIdDesc();

	List<Activity> findByType(UpdateType type);

}
