package com.freifeld.tools.quephaestus.core.exceptions;

import java.nio.file.Path;

public class TemplateFolderDoesNotExistException extends HandledQuephaestusException {
    public static final String MESSAGE_FORMAT = "Configuration [%s] contains a template folder that does not exist: %s";

    public TemplateFolderDoesNotExistException(Path templateFolder, Path configPath) {
        super(MESSAGE_FORMAT.formatted(configPath, templateFolder));
    }
}
