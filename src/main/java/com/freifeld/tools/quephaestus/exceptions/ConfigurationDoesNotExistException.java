package com.freifeld.tools.quephaestus.exceptions;

public class ConfigurationDoesNotExistException extends HandledQuephaestusException {
    public static final String MESSAGE_FORMAT = "Provided configuration file does not exist: %s";
    public ConfigurationDoesNotExistException(String path) {
        super(MESSAGE_FORMAT.formatted(path));
    }
}
