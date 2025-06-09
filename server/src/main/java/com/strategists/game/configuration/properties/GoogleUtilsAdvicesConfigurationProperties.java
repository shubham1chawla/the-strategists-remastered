package com.strategists.game.configuration.properties;

import jakarta.validation.constraints.NotBlank;

public record GoogleUtilsAdvicesConfigurationProperties(boolean bypassGoogleDriveSyncForTesting,
                                                        @NotBlank String command, @NotBlank String driveFolderId) {

}
