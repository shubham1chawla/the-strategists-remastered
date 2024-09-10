package com.strategists.game.configuration.properties;

import javax.validation.constraints.NotBlank;

public record GoogleUtilsAdvicesConfigurationProperties(boolean bypassGoogleDriveSyncForTesting,
		@NotBlank String command, @NotBlank String driveFolderId) {

}
