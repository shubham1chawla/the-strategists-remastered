package com.strategists.game.configuration.properties;

import java.io.File;
import java.nio.file.Paths;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "strategists.google.utils.permissions")
public record GoogleUtilsPermissionsConfigurationProperties(@NotBlank String command,
		@DefaultValue @Valid SimpleFileExportConfigurationProperties export,
		@DefaultValue @Valid Spreadsheet spreadsheet) {

	public record Spreadsheet(@NotBlank String id, @NotBlank String range) {

	}

	public File getExportFile() {
		return Paths.get(export.directory().getAbsolutePath(), export.fileName()).toFile();
	}

}
