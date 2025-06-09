package com.strategists.game.configuration.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import java.io.File;

@Validated
public record SimpleFileExportConfigurationProperties(File directory, @NotBlank String fileName) {

}
