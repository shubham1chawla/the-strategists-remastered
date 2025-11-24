package com.strategists.game.configuration.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "strategists")
public record StrategistsConfigurationProperties(@DefaultValue @Valid GameConfigurationProperties game,
                                                 @DefaultValue @Valid CleanUpConfigurationProperties cleanUp,
                                                 @DefaultValue @Valid SkipPlayerConfigurationProperties skipPlayer,
                                                 @DefaultValue @Valid PermissionsConfigurationProperties permissions,
                                                 @DefaultValue @Valid StorageConfigurationProperties storage,
                                                 @DefaultValue @Valid HistoryConfigurationProperties history,
                                                 @DefaultValue @Valid PredictionsConfigurationProperties predictions) {


    @AssertTrue(message = "History's Google Drive sync enabled but Storage's upload or download APIs are not enabled! " +
            "Either disable History's Google Drive sync or enable Storage's download and upload APIs!")
    boolean isHistoryGoogleDriveValid() {
        return !history.googleDrive().enabled() || (storage.downloadApi().enabled() && storage.uploadApi().enabled());
    }

    @AssertTrue(message = "Predictions' legacy data download enabled but Storage's download API is not enabled! " +
            "Either disable Predictions' legacy data download or enable Storage's download API!")
    boolean isLegacyPredictionsGoogleDriveValid() {
        return !predictions.legacy().googleDrive().enabled() || storage.downloadApi().enabled();
    }

}
