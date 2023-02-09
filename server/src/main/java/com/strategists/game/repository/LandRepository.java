package com.strategists.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.strategists.game.entity.Land;

public interface LandRepository extends JpaRepository<Land, Long> {

}
