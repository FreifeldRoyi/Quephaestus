package com.freifeld.tools.quephaestus.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Map;
import java.util.Optional;

public record QuephaestusConfiguration(
        Optional<@NotBlank String> project,
        Optional<@NotBlank String> namespace,

        @NotBlank
        String templatesFolder,

        /*
         * Relative directory location under working directory to take into consideration
         * e.g.
         * In Java, Generally speaking, code resides under src/main/java/...
         * In this case, src/main/java might be a good candidate for a base directory
         */
//        Optional<String> baseDirectory, TODO unused for now


        @NotEmpty
        Map<String, @Valid Element> elements,
        Map<String, @Valid BlueprintDefinition> blueprints,

        /*
         * Note that if provided with an absolute path the script path will remain the same,
         *  If given a relative path, it will be relative to the **working** directory only
         */
        Optional<String> preForgeScript,
        Optional<String> postForgeScript
) {
}
