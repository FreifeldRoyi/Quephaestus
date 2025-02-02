package com.freifeld.tools.quephaestus.exceptions;

import java.util.Set;
import java.util.function.Function;

public class MissingDataException extends QuephaestusException {
    public static final String MESSAGE_FORMAT = "Missing interpolation values. Add to payload file ('-p'), data option ('-d') or use '-i' for interactive mode:%n%s";

    private final Set<String> missing;

    public MissingDataException(Set<String> missing) {
        super(createMessage(missing));
        this.missing = missing;
    }

    private static String createMessage(Set<String> missing) {
        final var listDisplay = valuesListAsDisplayString(missing.stream(), Function.identity());
        return MESSAGE_FORMAT.formatted(listDisplay);
    }
}
