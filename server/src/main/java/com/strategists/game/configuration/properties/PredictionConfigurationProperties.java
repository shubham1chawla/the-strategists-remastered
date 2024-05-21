package com.strategists.game.configuration.properties;

import java.io.File;
import java.util.List;

import javax.validation.Valid;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "strategists.prediction")
@ConditionalOnProperty(name = "strategists.prediction.enabled", havingValue = "true")
public record PredictionConfigurationProperties(@DefaultValue @Valid PythonConfigurationProperties python,
		@DefaultValue @Valid SimpleFileExportConfigurationProperties export,
		@DefaultValue @Valid PredictionTrainConfigurationProperties train,
		@DefaultValue @Valid PredictionPredictConfigurationProperties predict) {

	public List<File> getAllDirectories() {
		return List.of(

				// Classifier's export directory
				export.directory(),

				// Game's data directory
				train.directory().data(),

				// Training results' metadata directory
				train.directory().metadata(),

				// Prediction file's test directory
				predict.directory().test());
	}

}
