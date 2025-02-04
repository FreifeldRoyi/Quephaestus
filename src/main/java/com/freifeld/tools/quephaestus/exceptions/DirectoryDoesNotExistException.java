package com.freifeld.tools.quephaestus.exceptions;

import java.nio.file.Path;

public class DirectoryDoesNotExistException extends QuephaestusException {
    public static final String MESSAGE_FORMAT = "Directory does not exist: %s";

    public DirectoryDoesNotExistException(Path path) {
        super(createMessage(path));
    }

    private static String createMessage(Path path) {
        return MESSAGE_FORMAT.formatted(path);
    }
}
