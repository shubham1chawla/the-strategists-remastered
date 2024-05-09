package com.strategists.game.configuration.properties;

import java.io.File;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "strategists.google.utils")
public record GoogleUtilsConfigurationProperties(File credentialsJsonFile,
		@DefaultValue @Valid PythonConfigurationProperties python,
		@DefaultValue @Valid GoogleUtilsPermissionsConfigurationProperties permissions) {

	@AssertTrue(message = "Credentials JSON must exists and must ends with '.json' extension!")
	boolean isCredentialsJsonFileValid() {
		return credentialsJsonFile.exists() && credentialsJsonFile.isFile()
				&& credentialsJsonFile.getPath().endsWith(".json");
	}

	public String getPythonExecutablePath() {
		return python.executable().getPath();
	}

	public String getPythonScriptPath() {
		return python.script().getPath();
	}

	public String getPermissionsCommand() {
		return permissions.command();
	}

	public String getCredentialsJsonFilePath() {
		return credentialsJsonFile.getPath();
	}

	public String getPermissionsSpreadsheetId() {
		return permissions.spreadsheet().id();
	}

	public String getPermissionsSpreadsheetRange() {
		return permissions.spreadsheet().range();
	}

	public String getPermissionsExportDirectoryPath() {
		return permissions.export().directory().getPath();
	}

}
