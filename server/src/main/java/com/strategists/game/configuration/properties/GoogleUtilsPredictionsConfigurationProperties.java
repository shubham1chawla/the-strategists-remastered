package com.strategists.game.configuration.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.bind.DefaultValue;

public record GoogleUtilsPredictionsConfigurationProperties(boolean bypassGoogleDriveSyncForTesting,
                                                            @NotBlank String command,
                                                            @DefaultValue @Valid SubCommandConfigurationProperties download,
                                                            @DefaultValue @Valid SubCommandConfigurationProperties upload) {

    public record SubCommandConfigurationProperties(@NotBlank String subCommand, @NotBlank String driveFolderId) {

    }

}
