package com.freifeld.tools.quephaestus.core.exceptions;

import java.util.Set;
import java.util.function.Function;

public class InvalidParameterForCommandException extends HandledQuephaestusException {
    public static final String MESSAGE_FORMAT = """
            '%s' is not a valid parameter for the '%s' command.
            Possible values are:
            %s
            """.strip();

    public InvalidParameterForCommandException(String commandName, String parameter, Set<String> possibleValues) {
        super(createMessage(commandName, parameter, possibleValues));
    }

    private static String createMessage(String commandName, String parameter, Set<String> possibleValues) {
        return MESSAGE_FORMAT.formatted(
                parameter,
                commandName,
                valuesListAsDisplayString(possibleValues.stream(), Function.identity())
        );
    }
}
