package com.strategists.game.configuration.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.io.File;
import java.nio.file.Paths;

@Validated
@ConfigurationProperties(prefix = "strategists.google.utils.permissions")
public record GoogleUtilsPermissionsConfigurationProperties(boolean bypassGoogleSheetsQueryForTesting,
                                                            @NotBlank String command,
                                                            @DefaultValue @Valid SimpleFileExportConfigurationProperties export,
                                                            @DefaultValue @Valid Spreadsheet spreadsheet) {

    public record Spreadsheet(@NotBlank String id, @NotBlank String range) {

    }

    public File getExportFile() {
        return Paths.get(export.directory().getAbsolutePath(), export.fileName()).toFile();
    }

}
