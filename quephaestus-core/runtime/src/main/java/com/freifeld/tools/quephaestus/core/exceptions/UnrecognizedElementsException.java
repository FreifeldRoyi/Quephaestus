package com.freifeld.tools.quephaestus.core.exceptions;

import java.util.Map;
import java.util.Set;

public class UnrecognizedElementsException extends HandledQuephaestusException {
    public static final String MESSAGE_FORMAT = """
            Unrecognized element(s) in element group(s):
            %s
            """;

    public UnrecognizedElementsException(Map<String, Set<String>> missingElementsPerGroup) {
        super(createMessage(missingElementsPerGroup));
    }

    private static String createMessage(Map<String, Set<String>> missingElements) {
        return MESSAGE_FORMAT.formatted(
                valuesListAsDisplayString(
                        missingElements.entrySet().stream(),
                        entry -> "%s: %s".formatted(entry.getKey(), entry.getValue())
                )
        );
    }
}
