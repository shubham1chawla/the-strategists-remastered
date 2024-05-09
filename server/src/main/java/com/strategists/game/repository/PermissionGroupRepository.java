package com.strategists.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.strategists.game.entity.PermissionGroup;

public interface PermissionGroupRepository extends JpaRepository<PermissionGroup, String> {

}
