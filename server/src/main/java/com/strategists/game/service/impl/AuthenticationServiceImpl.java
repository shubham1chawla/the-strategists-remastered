package com.strategists.game.service.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.strategists.game.request.GoogleRecaptchaVerificationRequest;
import com.strategists.game.response.GoogleRecaptchaVerificationResponse;
import com.strategists.game.service.AuthenticationService;

import lombok.val;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	@Value("${strategists.security.google-recaptcha.api-url}")
	private String googleRecaptchaAPIUrl;

	@Value("${strategists.security.google-recaptcha.secret-key}")
	private String googleRecaptchaSecretKey;

	@PostConstruct
	public void setup() {
		Assert.hasText(googleRecaptchaAPIUrl, "Google Recaptcha API URL is not provided!");
		Assert.hasText(googleRecaptchaSecretKey, "Google Recaptcha Secret key is not provided!");
	}

	@Override
	public boolean verify(GoogleRecaptchaVerificationRequest request) {
		val headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

		val body = new LinkedMultiValueMap<String, String>();
		body.add("secret", googleRecaptchaSecretKey);
		body.add("response", request.getClientToken());

		val entity = new HttpEntity<>(body, headers);
		val template = new RestTemplate();
		val response = template.postForEntity(googleRecaptchaAPIUrl, entity, GoogleRecaptchaVerificationResponse.class);
		return response.hasBody() && response.getBody().isSuccess();
	}

}
