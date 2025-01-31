package com.freifeld.tools.quephaestus.exceptions;

import java.util.Map;
import java.util.Set;

public class UnrecognizedElementsException extends QuephaestusException {
    public static final String MESSAGE_FORMAT = "One or more blueprint definitions contains unrecognized elements [%s]:%n%s";

    private final Map<String, Set<String>> missingElements;

    public UnrecognizedElementsException(Map<String, Set<String>> missingElements, String configurationPath) {
        super(createMessage(missingElements, configurationPath));
        this.missingElements = missingElements;

    }

    private static String createMessage(Map<String, Set<String>> missingElements, String configurationPath) {
        final var listDisplay = valuesListAsDisplayString(
                missingElements.entrySet().stream(),
                entry -> "%s: %s".formatted(entry.getKey(), entry.getValue())
        );

        return MESSAGE_FORMAT.formatted(configurationPath, listDisplay);
    }

}
