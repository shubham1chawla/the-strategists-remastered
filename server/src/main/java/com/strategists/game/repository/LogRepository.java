package com.strategists.game.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.strategists.game.entity.Log;

public interface LogRepository extends JpaRepository<Log, Long> {

	List<Log> findByOrderByIdDesc();

}
