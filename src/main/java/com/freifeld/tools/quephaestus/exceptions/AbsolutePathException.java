package com.freifeld.tools.quephaestus.exceptions;

import java.nio.file.Path;

public class AbsolutePathException extends HandledQuephaestusException {
    public static final String MESSAGE_FORMAT = "Please provide a relative path. Given: %s";

    public AbsolutePathException(Path path) {
        super(createMessage(path.toString()));
    }

    public AbsolutePathException(String path) {
        super(path);
    }

    private static String createMessage(String path) {
        return MESSAGE_FORMAT.formatted(path);
    }
}
