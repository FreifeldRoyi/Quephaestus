package com.freifeld.tools.quephaestus.core;

import com.freifeld.tools.quephaestus.core.configuration.QuephaestusConfiguration;
import com.freifeld.tools.quephaestus.core.exceptions.*;
import jakarta.validation.Validation;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

public class Validators {

    public static void validateAbsolutePath(Path path) {
        if (!path.isAbsolute()) {
            throw new RelativePathException(path);
        }
    }

    public static void validateRelativePath(Path path) {
        if (path.isAbsolute()) {
            throw new AbsolutePathException(path);
        }
    }

    public static void directoryExists(Path path) {
        if (!Files.isDirectory(path)) {
            throw new PathDoesNotExistException(path);
        }
    }

    public static void validateConfiguration(QuephaestusConfiguration config) {
        // 1. Simple Bean validation
        try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            final var validator = validatorFactory.getValidator();
            final var violations = validator.validate(config);

            if (!violations.isEmpty()) {
                throw new ConfigurationValidationException(violations);
            }
        }

        // 2. Validate that each blueprint's elements appear on the elementNames list
        final var elementNames = config.elements().keySet();
        final var missingElements = config.elementGroups().entrySet().stream()
                .filter((definition) -> !elementNames.containsAll(definition.getValue().elements()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().elements().stream().filter(element -> !elementNames.contains(element)).collect(Collectors.toSet())
                ));
        if (!missingElements.isEmpty()) {
            throw new UnrecognizedElementsException(missingElements);
        }
    }
}
