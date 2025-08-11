package com.strategists.game.service;

import com.strategists.game.response.PermissionGroupResponse;

import java.util.Optional;

public interface PermissionsService {

    boolean verifyGoogleRecaptcha(String clientToken);

    Optional<PermissionGroupResponse> getPermissionGroupByEmail(String email);

}
