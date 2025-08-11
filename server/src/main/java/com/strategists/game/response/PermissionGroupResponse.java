package com.strategists.game.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PermissionGroupResponse {

    public enum PermissionStatus {
        ENABLED, DISABLED;
    }

    private String email;
    private PermissionStatus gameCreation;

    public static PermissionGroupResponse allowAll(String email) {
        var response = new PermissionGroupResponse();
        response.setEmail(email);
        response.setGameCreation(PermissionStatus.ENABLED);
        return response;
    }

}
