package com.freifeld.tools.quephaestus.exceptions;

import java.nio.file.Path;

public class PathDoesNotExistException extends HandledQuephaestusException {
    public static final String MESSAGE_FORMAT = "Path does not exist: %s";

    public PathDoesNotExistException(Path path) {
        super(createMessage(path));
    }

    private static String createMessage(Path path) {
        return MESSAGE_FORMAT.formatted(path);
    }
}
