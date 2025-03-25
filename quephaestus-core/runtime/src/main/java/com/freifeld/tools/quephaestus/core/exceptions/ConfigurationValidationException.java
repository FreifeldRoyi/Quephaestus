package com.freifeld.tools.quephaestus.core.exceptions;

import com.freifeld.tools.quephaestus.core.configuration.QuephaestusConfiguration;
import jakarta.validation.ConstraintViolation;

import java.util.Set;
import java.util.stream.Collectors;

public class ConfigurationValidationException extends HandledQuephaestusException {
    public static final String MESSAGE_FORMAT = "Configuration validation failed:%n%s";

    private final Set<ConstraintViolation<QuephaestusConfiguration>> violations;

    public ConfigurationValidationException(Set<ConstraintViolation<QuephaestusConfiguration>> violations) {
        super(createMessage(violations));
        this.violations = violations;
    }

    private static String createMessage(Set<ConstraintViolation<QuephaestusConfiguration>> violations) {
        final var byField = violations.stream()
                .collect(Collectors.groupingBy(violation -> violation.getPropertyPath().toString(),
                        Collectors.mapping(ConstraintViolation::getMessage, Collectors.toSet())));
        final var violationsDisplay = valuesListAsDisplayString(byField.entrySet().stream(),
                entry -> "%s: %s".formatted(entry.getKey(), entry.getValue()));
        return MESSAGE_FORMAT.formatted(violationsDisplay);
    }

    public Set<ConstraintViolation<QuephaestusConfiguration>> getViolations() {
        return this.violations;
    }
}
