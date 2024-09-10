package com.strategists.game.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
		val output = ScriptUtil.execute(googleUtils.getPermissionsCommands());
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
	public void downloadGameCSVFiles(File directory) {
		log.info("Downloading Game CSV files from Google Drive to {}", directory.getPath());
		syncGameCSVFiles(directory, false);
	}

	@Override
	public void uploadGameCSVFiles(File directory) {
		log.info("Uploading Game CSV files to Google Drive from {}", directory.getPath());
		syncGameCSVFiles(directory, true);
	}

	private void syncGameCSVFiles(File directory, boolean upload) {
		val googleUtils = properties.utils();
		val commands = googleUtils.getPredictionsCommands(directory, upload);
		val bypass = googleUtils.predictions().bypassGoogleDriveSyncForTesting();
		executeSyncScript(commands, bypass);
	}

	@Override
	public void uploadAdviceCSVFiles(File directory) {
		log.info("Uploading Advice CSV files to Google Drive from {}", directory.getPath());
		val googleUtils = properties.utils();
		val commands = googleUtils.getAdvicesCommands(directory);
		val bypass = googleUtils.advices().bypassGoogleDriveSyncForTesting();
		executeSyncScript(commands, bypass);
	}

	private void executeSyncScript(String[] commands, boolean bypass) {
		// Checking if user bypassed google drive sync for testing
		if (bypass) {
			log.warn("Bypassing syncing Game CSV files! ONLY DO THIS FOR TESTING!");
			return;
		}
		val output = ScriptUtil.execute(commands);
		log.info("Script Output:{}{}", System.lineSeparator(), String.join(System.lineSeparator(), output));
	}

}
