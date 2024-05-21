package com.strategists.game.configuration.properties;

import java.io.File;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "strategists.prediction.train")
public record PredictionTrainConfigurationProperties(@NotBlank String command,
		@DefaultValue @Valid Directory directory) {

	public record Directory(File data, File metadata) {

	}

}
