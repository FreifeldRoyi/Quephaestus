package com.freifeld.tools.quephaestus.core.exceptions;

import java.nio.file.Path;

public class RelativePathException extends HandledQuephaestusException {
    public static final String MESSAGE_FORMAT = "Please provide an absolute path. Given: %s";

    public RelativePathException(Path path) {
        super(createMessage(path.toString()));
    }

    public RelativePathException(String path) {
        super(path);
    }

    private static String createMessage(String path) {
        return MESSAGE_FORMAT.formatted(path);
    }
}
