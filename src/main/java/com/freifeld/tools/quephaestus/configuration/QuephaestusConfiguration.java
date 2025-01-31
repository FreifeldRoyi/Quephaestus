package com.freifeld.tools.quephaestus.configuration;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;
import java.util.Optional;

public record QuephaestusConfiguration(
        Optional<@NotBlank String> project,
        Optional<@NotBlank String> namespace,
        String templatesFolder,
        /**
         * Relative directory location under working directory to take into consideration
         * e.g.
         * In Java, Generally speaking, code resides under src/main/java/...
         * In this case, src/main/java might be a good candidate for a base directory
         */
        Optional<String> baseDirectory,
        Map<String, Element> elements,
        Map<String, BlueprintDefinition> blueprints
) {
}
