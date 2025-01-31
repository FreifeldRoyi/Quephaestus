package com.freifeld.tools.quephaestus.exceptions;

import com.freifeld.tools.quephaestus.configuration.QuephaestusConfiguration;
import jakarta.validation.ConstraintViolation;

import java.util.Set;
import java.util.stream.Collectors;

public class ConfigurationValidationException extends QuephaestusException {
    public static final String MESSAGE_FORMAT = "Configuration validation failed [%s]:%n%s";

    private final Set<ConstraintViolation<QuephaestusConfiguration>> violations;
    private final String configurationPath;

    public ConfigurationValidationException(Set<ConstraintViolation<QuephaestusConfiguration>> violations, String configurationPath) {
        super(createMessage(violations, configurationPath));
        this.violations = violations;
        this.configurationPath = configurationPath;
    }

    private static String createMessage(Set<ConstraintViolation<QuephaestusConfiguration>> violations, String configurationPath) {
        final var byField = violations.stream()
                .collect(Collectors.groupingBy(violation -> violation.getPropertyPath().toString(),
                        Collectors.mapping(ConstraintViolation::getMessage, Collectors.toSet())));
        final var violationsDisplay = valuesListAsDisplayString(byField.entrySet().stream(),
                entry -> "%s: %s".formatted(entry.getKey(), entry.getValue()));
        return MESSAGE_FORMAT.formatted(configurationPath, violationsDisplay);
    }
}
