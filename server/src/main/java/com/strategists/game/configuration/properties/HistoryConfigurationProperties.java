package com.strategists.game.configuration.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.io.File;

@Validated
@ConfigurationProperties(prefix = "strategists.history")
public record HistoryConfigurationProperties(File dataDirectory, @DefaultValue @Valid GoogleDrive googleDrive) {

    public record GoogleDrive(boolean enabled, String folderId) {

        @AssertTrue(message = "History upload enabled! Google Drive folder ID required!")
        public boolean isFolderIdValid() {
            return !enabled || StringUtils.hasText(folderId);
        }

    }

}
