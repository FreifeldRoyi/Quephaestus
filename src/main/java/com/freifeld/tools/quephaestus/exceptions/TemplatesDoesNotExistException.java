package com.freifeld.tools.quephaestus.exceptions;

import java.nio.file.Path;
import java.util.Set;

public class TemplatesDoesNotExistException extends QuephaestusException {
    public static final String MESSAGE_FORMAT = """
            Could not find the requested template(s).
            Given   (%d): %s
            Missing (%d):
            %s
            """.strip();

    public TemplatesDoesNotExistException(Set<String> expected, Set<Path> missing) {
        super(createMessage(expected, missing));
    }

    private static String createMessage(Set<String> expected, Set<Path> missing) {
        final var message = MESSAGE_FORMAT.formatted(
                expected.size(),
                expected,
                missing.size(),
                QuephaestusException.valuesListAsDisplayString(missing.stream(), Path::toString));
        return message;
    }
}
