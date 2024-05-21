package com.strategists.game.service.impl;

import java.io.IOException;
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
import com.strategists.game.repository.PermissionGroupRepository;
import com.strategists.game.request.GoogleRecaptchaVerificationRequest;
import com.strategists.game.response.GoogleRecaptchaVerificationResponse;
import com.strategists.game.service.AuthenticationService;
import com.strategists.game.service.PermissionsService;
import com.strategists.game.util.ScriptUtil;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class GoogleAPIServiceImpl implements AuthenticationService, PermissionsService {

	@Autowired
	private GoogleConfigurationProperties properties;

	@Autowired
	private PermissionGroupRepository permissionGroupRepository;

	@PostConstruct
	public void setup() throws IOException {

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
	public Optional<PermissionGroup> getPermissionGroupByEmail(String email) {
		return permissionGroupRepository.findById(email);
	}

	private void loadPermissionGroups() throws IOException {

		val googleUtils = properties.utils();

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
		log.info("Permissions Script Output:{}{}", System.lineSeparator(), String.join(System.lineSeparator(), output));

		// Validating whether permissions file exists
		val permissionGroupsFile = properties.utils().permissions().getExportFile();
		Assert.isTrue(permissionGroupsFile.exists(), "Permission Groups JSON not generated!");

		// Reading permissions JSON and saving permission groups
		try {
			val permissionsGroups = new ObjectMapper().readValue(permissionGroupsFile, PermissionGroup[].class);
			permissionGroupRepository.saveAll(Arrays.asList(permissionsGroups));

			log.info("Saved {} permission groups", permissionsGroups.length);
		} finally {
			permissionGroupsFile.delete();
		}

	}

}
