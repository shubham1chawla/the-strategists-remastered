package com.strategists.game.configuration.properties;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.bind.DefaultValue;

public record GoogleUtilsPredictionsConfigurationProperties(@NotBlank String command,
		@DefaultValue @Valid SubCommandConfigurationProperties download,
		@DefaultValue @Valid SubCommandConfigurationProperties upload) {

	public record SubCommandConfigurationProperties(@NotBlank String subCommand, @NotBlank String driveFolderId) {

	}

}
