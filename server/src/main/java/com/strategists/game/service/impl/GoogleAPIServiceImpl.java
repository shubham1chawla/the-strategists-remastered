package com.strategists.game.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strategists.game.configuration.properties.GoogleConfigurationProperties;
import com.strategists.game.entity.PermissionGroup;
import com.strategists.game.exception.FailedProcessException;
import com.strategists.game.repository.PermissionGroupRepository;
import com.strategists.game.request.GoogleRecaptchaVerificationRequest;
import com.strategists.game.response.GoogleRecaptchaVerificationResponse;
import com.strategists.game.service.AuthenticationService;
import com.strategists.game.service.DataSyncService;
import com.strategists.game.service.PermissionsService;
import com.strategists.game.util.ScriptUtil;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class GoogleAPIServiceImpl implements AuthenticationService, PermissionsService, DataSyncService {

	@Autowired
	private GoogleConfigurationProperties properties;

	@Autowired
	private PermissionGroupRepository permissionGroupRepository;

	@PostConstruct
	public void setup() {

		// Loading permission groups from Google Sheets
		loadPermissionGroups();
	}

	@Override
	public boolean verify(GoogleRecaptchaVerificationRequest request) {
		val headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

		val body = new LinkedMultiValueMap<String, String>();
		body.add("secret", properties.recaptcha().secretKey());
		body.add("response", request.getClientToken());

		val entity = new HttpEntity<>(body, headers);
		val template = new RestTemplate();
		val response = template.postForEntity(properties.recaptcha().apiUrl(), entity,
				GoogleRecaptchaVerificationResponse.class);
		return response.hasBody() && response.getBody().isSuccess();
	}

	@Override
	public void loadPermissionGroups() {

		val googleUtils = properties.utils();

		// Checking if user bypassed google sheets query for testing
		if (googleUtils.permissions().bypassGoogleSheetsQueryForTesting()) {
			log.warn("Bypassing querying permission groups! ONLY DO THIS FOR TESTING!");

			// Loading from permissions file in export directory
			val permissionGroupsFile = googleUtils.permissions().getExportFile();

			// Checking if testing permission file exists
			Assert.isTrue(permissionGroupsFile.exists(),
					"You've set 'bypass-google-sheets-query-for-testing', but there is no '"
							+ googleUtils.permissions().export().fileName() + "' in the '"
							+ googleUtils.permissions().export().directory().getPath()
							+ "' directory! Fix this issue by placing a testing permissions file to load!");

			loadPermissionGroups(permissionGroupsFile);
			return;
		}

		log.info("Querying permission groups...");

		// Executing permissions script
		val output = ScriptUtil.execute(

				// Path to executable
				googleUtils.python().executable().getPath(),

				// Path to google-utils script
				googleUtils.python().script().getPath(),

				// Permissions command
				googleUtils.permissions().command(),

				// Adding Credentials JSON argument
				"--credentials-json", googleUtils.credentialsJsonFile().getPath(),

				// Adding Google's Spreadsheet ID argument
				"--spreadsheet-id", googleUtils.permissions().spreadsheet().id(),

				// Adding Google's Spreadsheet Range argument
				"--spreadsheet-range", googleUtils.permissions().spreadsheet().range(),

				// Adding export directory argument
				"--export-dir", googleUtils.permissions().export().directory().getPath()

		);
		log.info("Script Output:{}{}", System.lineSeparator(), String.join(System.lineSeparator(), output));

		// Loading from permissions file in export directory
		val permissionGroupsFile = googleUtils.permissions().getExportFile();
		loadPermissionGroups(permissionGroupsFile);

	}

	private void loadPermissionGroups(File permissionGroupsFile) {

		// Validating whether permissions file exists
		Assert.isTrue(permissionGroupsFile.exists(), "Permission Groups JSON not generated!");

		// Reading permissions JSON and saving permission groups
		try {
			val permissionGroups = new ObjectMapper().readValue(permissionGroupsFile, PermissionGroup[].class);
			permissionGroupRepository.saveAll(Arrays.asList(permissionGroups));

			log.info("Saved {} permission groups", permissionGroups.length);
		} catch (IOException ex) {
			log.error("Unable to load permission groups! Message: {}", ex.getMessage());
			log.debug(ex);
			throw new FailedProcessException(ex);
		} finally {

			// Deleting permissions file only if bypass is disabled
			val bypass = properties.utils().permissions().bypassGoogleSheetsQueryForTesting();
			if (!bypass && permissionGroupsFile.exists()) {
				try {
					Files.delete(permissionGroupsFile.toPath());
					log.info("Removed: {}", permissionGroupsFile.getPath());
				} catch (IOException ex) {
					log.warn("Unable to remove: {} | Message: {}", ex.getMessage());
					log.debug(ex);
				}
			} else if (bypass) {
				log.info("Skipped removing: {}", permissionGroupsFile.getPath());
			}
		}

	}

	@Override
	public Optional<PermissionGroup> getPermissionGroupByEmail(String email) {
		return permissionGroupRepository.findById(email);
	}

	@Override
	public void downloadCSVFiles(File directory) {
		syncCSVFiles(directory, false);
	}

	@Override
	public void uploadCSVFiles(File directory) {
		syncCSVFiles(directory, true);
	}

	private void syncCSVFiles(File directory, boolean upload) {

		// Validating directory
		Assert.isTrue(directory.exists(), "Data directory must exists!");
		Assert.isTrue(directory.isDirectory(), "Data directory must not be a file!");

		val googleUtils = properties.utils();

		// Creating script's commands
		val commands = new ArrayList<String>();

		// Path to executable
		commands.add(googleUtils.python().executable().getPath());

		// Path to google-utils script
		commands.add(googleUtils.python().script().getPath());

		// Predictions command
		commands.add(googleUtils.predictions().command());

		// Adding sub-command
		commands.add((upload ? googleUtils.predictions().upload() : googleUtils.predictions().download()).subCommand());

		// Adding Credentials JSON argument
		commands.add("--credentials-json");
		commands.add(googleUtils.credentialsJsonFile().getPath());

		// Adding download folder ID argument
		commands.add("--download-folder-id");
		commands.add(googleUtils.predictions().download().driveFolderId());

		// Adding upload folder ID argument
		if (upload) {
			commands.add("--upload-folder-id");
			commands.add(googleUtils.predictions().upload().driveFolderId());
		}

		// Adding data directory argument
		commands.add("--game-data-dir");
		commands.add(directory.getPath());
		log.debug("Google Drive Sync Commands: {}", commands);

		// Executing download script
		val output = ScriptUtil.execute(commands.toArray(String[]::new));
		log.info("Script Output:{}{}", System.lineSeparator(), String.join(System.lineSeparator(), output));

	}

}
