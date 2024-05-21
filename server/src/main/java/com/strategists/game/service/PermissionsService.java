package com.strategists.game.service;

import java.util.Optional;

import com.strategists.game.entity.PermissionGroup;

public interface PermissionsService {

	void loadPermissionGroups();

	Optional<PermissionGroup> getPermissionGroupByEmail(String email);

}
