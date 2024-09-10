package com.strategists.game.configuration.properties;

import java.io.File;
import java.util.ArrayList;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import lombok.val;

@Validated
@ConfigurationProperties(prefix = "strategists.google.utils")
public record GoogleUtilsConfigurationProperties(File credentialsJsonFile,
		@DefaultValue @Valid PythonConfigurationProperties python,
		@DefaultValue @Valid GoogleUtilsPermissionsConfigurationProperties permissions,
		@DefaultValue @Valid GoogleUtilsPredictionsConfigurationProperties predictions,
		@DefaultValue @Valid GoogleUtilsAdvicesConfigurationProperties advices) {

	private class Arguments {
		public static final String CREDENTIALS_JSON = "--credentials-json";
		public static final String SPREADSHEET_ID = "--spreadsheet-id";
		public static final String SPREADSHEET_RANGE = "--spreadsheet-range";
		public static final String EXPORT_DIR = "--export-dir";
		public static final String DOWNLOAD_FOLDER_ID = "--download-folder-id";
		public static final String UPLOAD_FOLDER_ID = "--upload-folder-id";
		public static final String GAME_DATA_DIR = "--game-data-dir";
		public static final String ADVICE_DATA_DIR = "--advice-data-dir";
	}

	@AssertTrue(message = "Credentials JSON must exists and must ends with '.json' extension!")
	boolean isCredentialsJsonFileValid() {
		return credentialsJsonFile.exists() && credentialsJsonFile.isFile()
				&& credentialsJsonFile.getPath().endsWith(".json");
	}

	public String[] getPermissionsCommands() {
		return new String[] {
				// Path to executable
				python.executable().getPath(),

				// Path to google-utils script
				python.script().getPath(),

				// Permissions command
				permissions.command(),

				// Adding Credentials JSON argument
				Arguments.CREDENTIALS_JSON, credentialsJsonFile.getPath(),

				// Adding Google's Spreadsheet ID argument
				Arguments.SPREADSHEET_ID, permissions.spreadsheet().id(),

				// Adding Google's Spreadsheet Range argument
				Arguments.SPREADSHEET_RANGE, permissions.spreadsheet().range(),

				// Adding export directory argument
				Arguments.EXPORT_DIR, permissions.export().directory().getPath() };
	}

	public String[] getPredictionsCommands(File directory, boolean upload) {
		// Creating script's commands
		val commands = new ArrayList<String>();

		// Path to executable
		commands.add(python.executable().getPath());

		// Path to google-utils script
		commands.add(python.script().getPath());

		// Predictions command
		commands.add(predictions.command());

		// Adding sub-command
		commands.add((upload ? predictions.upload() : predictions.download()).subCommand());

		// Adding Credentials JSON argument
		commands.add(Arguments.CREDENTIALS_JSON);
		commands.add(credentialsJsonFile.getPath());

		// Adding download folder ID argument
		commands.add(Arguments.DOWNLOAD_FOLDER_ID);
		commands.add(predictions.download().driveFolderId());

		// Adding upload folder ID argument
		if (upload) {
			commands.add(Arguments.UPLOAD_FOLDER_ID);
			commands.add(predictions.upload().driveFolderId());
		}

		// Adding game data directory argument
		commands.add(Arguments.GAME_DATA_DIR);
		commands.add(directory.getPath());

		return commands.toArray(String[]::new);
	}

	public String[] getAdvicesCommands(File directory) {
		return new String[] {
				// Path to executable
				python.executable().getPath(),

				// Path to google-utils script
				python.script().getPath(),

				// Advice command
				advices.command(),

				// Adding Credentials JSON argument
				Arguments.CREDENTIALS_JSON, credentialsJsonFile.getPath(),

				// Adding upload folder ID argument
				Arguments.UPLOAD_FOLDER_ID, advices.driveFolderId(),

				// Adding advice data directory argument
				Arguments.ADVICE_DATA_DIR, directory.getPath() };
	}

}
