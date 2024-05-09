package com.strategists.game.exception;

import java.util.List;

import lombok.val;

public class FailedProcessException extends RuntimeException {

	private static final long serialVersionUID = -9109931405287768298L;

	public FailedProcessException(Throwable cause) {
		super(cause);
	}

	public FailedProcessException(int code, List<String> output) {
		super(formatMessage(code, output));
	}

	private static String formatMessage(int code, List<String> output) {
		val builder = new StringBuilder("Process ended with code: ");
		builder.append(code).append(System.lineSeparator()).append(String.join(System.lineSeparator(), output));
		return builder.toString();
	}

}
