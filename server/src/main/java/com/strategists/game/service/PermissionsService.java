package com.strategists.game.service;

import com.strategists.game.entity.PermissionGroup;

import java.util.Optional;

public interface PermissionsService {

    void loadPermissionGroups();

    Optional<PermissionGroup> getPermissionGroupByEmail(String email);

}
