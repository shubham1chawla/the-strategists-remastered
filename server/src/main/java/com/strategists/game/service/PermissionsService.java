package com.strategists.game.service;

import java.util.Optional;

import com.strategists.game.entity.PermissionGroup;

public interface PermissionsService {

	Optional<PermissionGroup> getPermissionGroupByEmail(String email);

}
