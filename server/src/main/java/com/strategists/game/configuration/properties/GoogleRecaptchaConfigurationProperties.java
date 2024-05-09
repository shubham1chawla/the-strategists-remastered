package com.strategists.game.configuration.properties;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "strategists.google.recaptcha")
public record GoogleRecaptchaConfigurationProperties(@NotBlank String apiUrl, @NotBlank String secretKey) {

	@AssertTrue(message = "API endpoint must be a valid URL!")
	boolean isAPIUrlValid() {
		return UrlValidator.getInstance().isValid(apiUrl);
	}

}
