package com.strategists.game.configuration.properties;

import jakarta.validation.constraints.AssertTrue;
import org.springframework.validation.annotation.Validated;

import java.io.File;

@Validated
public record PythonConfigurationProperties(File executable, File script) {

    @AssertTrue(message = "Python binary must exist and be executable!")
    boolean isExecutableValid() {
        return executable.exists() && executable.isFile() && executable.canExecute();
    }

    @AssertTrue(message = "Python script must exist and end with '.py' extension!")
    boolean isScriptValid() {
        return script.exists() && script.isFile() && script.getPath().endsWith(".py");
    }

}
