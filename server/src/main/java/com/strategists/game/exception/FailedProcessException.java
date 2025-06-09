package com.strategists.game.exception;

import java.io.Serial;
import java.util.List;

public class FailedProcessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -9109931405287768298L;

    public FailedProcessException(Throwable cause) {
        super(cause);
    }

    public FailedProcessException(int code, List<String> output) {
        super(formatMessage(code, output));
    }

    private static String formatMessage(int code, List<String> output) {
        return "Process ended with code: " + code + System.lineSeparator() + String.join(System.lineSeparator(), output);
    }

}
