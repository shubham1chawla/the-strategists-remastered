package com.strategists.game.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.strategists.game.exception.FailedProcessException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScriptUtil {

	public static List<String> execute(String... commands) throws FailedProcessException {
		log.debug("Executing script: {}", Arrays.toString(commands));

		// Setting up process builder
		val builder = new ProcessBuilder(commands);
		builder.redirectErrorStream(true);

		int code = 0;
		val output = new ArrayList<String>();
		try {

			// Starting the process
			val process = builder.start();
			code = process.waitFor();
			log.debug("Process exited with code: {}", code);

			// Extracting output from process' stream
			val stream = new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8);
			try (val reader = new BufferedReader(stream)) {
				reader.lines().forEach(output::add);
			}
			log.debug("Process output: {}", output);

		} catch (Exception ex) {
			log.error("Unable to start or wait for the process! Message: {}", ex.getMessage());
			log.debug(ex);
			throw new FailedProcessException(ex);
		}

		// Checking if process executed successfully
		if (code != 0) {
			throw new FailedProcessException(code, output);
		}

		// Returning output from the process
		return output;
	}

}
