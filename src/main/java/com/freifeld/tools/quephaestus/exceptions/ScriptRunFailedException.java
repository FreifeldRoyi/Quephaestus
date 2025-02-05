package com.freifeld.tools.quephaestus.exceptions;

public class ScriptRunFailedException extends HandledQuephaestusException {
    public static final String MESSAGE_FORMAT = "Provided script execution failed (code %d). Given: '%s'";

    public ScriptRunFailedException(int code, String script) {
        super(createMessage(code, script));
    }

    private static String createMessage(int code, String script) {
        return MESSAGE_FORMAT.formatted(code, script);
    }
}
