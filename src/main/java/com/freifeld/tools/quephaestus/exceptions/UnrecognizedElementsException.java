package com.freifeld.tools.quephaestus.exceptions;

import java.util.Map;
import java.util.Set;

public class UnrecognizedElementsException extends QuephaestusException {
    public static final String MESSAGE_FORMAT = """
            Unrecognized element(s) in blueprint(s) [%s]:
            %s
            """;

    public UnrecognizedElementsException(Map<String, Set<String>> missingElementsPerBlueprint, String configurationPath) {
        super(createMessage(missingElementsPerBlueprint, configurationPath));
    }

    private static String createMessage(Map<String, Set<String>> missingElements, String configurationPath) {
        return MESSAGE_FORMAT.formatted(
                configurationPath,
                valuesListAsDisplayString(
                        missingElements.entrySet().stream(),
                        entry -> "%s: %s".formatted(entry.getKey(), entry.getValue())
                )
        );
    }

}
