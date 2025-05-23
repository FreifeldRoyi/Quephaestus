package com.freifeld.tools.quephaestus.core.exceptions;

import java.nio.file.Path;

public class AbsolutePathException extends HandledQuephaestusException {
    public static final String MESSAGE_FORMAT = "Please provide a relative path. Given: %s";

    public AbsolutePathException(Path path) {
        super(createMessage(path.toString()));
    }

    public AbsolutePathException(String path) {
        this(Path.of(path));
    }

    private static String createMessage(String path) {
        return MESSAGE_FORMAT.formatted(path);
    }
}
