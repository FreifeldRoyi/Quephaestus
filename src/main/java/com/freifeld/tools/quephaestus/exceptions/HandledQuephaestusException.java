package com.freifeld.tools.quephaestus.exceptions;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HandledQuephaestusException extends QuephaestusException {
    public static <T> String valuesListAsDisplayString(Stream<T> collection, Function<T, String> elementDisplay) {
        return collection.map(t -> " - %s".formatted(elementDisplay.apply(t))).collect(Collectors.joining("\n"));
    }

    public HandledQuephaestusException(String message) {
        super(message);
    }
}
