package com.strategists.game.configuration.properties;

import javax.validation.Valid;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "strategists.google")
public record GoogleConfigurationProperties(@DefaultValue @Valid GoogleRecaptchaConfigurationProperties recaptcha,
		@DefaultValue @Valid GoogleUtilsConfigurationProperties utils) {

}
