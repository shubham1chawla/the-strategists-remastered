package com.strategists.game.configuration.properties;

import java.io.File;

import javax.validation.constraints.AssertTrue;

import org.springframework.validation.annotation.Validated;

@Validated
public record PythonConfigurationProperties(File executable, File script) {

	@AssertTrue(message = "Python executable must exists and must be executable!")
	boolean isExecutableValid() {
		return isValid(executable);
	}

	@AssertTrue(message = "Python script must be executable and ends with '.py' extension!")
	boolean isScriptValid() {
		return isValid(script) && script.getPath().endsWith(".py");
	}

	private boolean isValid(File file) {
		return file.exists() && file.isFile() && file.canExecute();
	}

}
