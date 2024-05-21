package com.strategists.game.configuration.properties;

import java.io.File;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

@Validated
public record SimpleFileExportConfigurationProperties(File directory, @NotBlank String fileName) {

}
