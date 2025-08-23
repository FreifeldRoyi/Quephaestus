package com.freifeld.tools.quephaestus.core.configuration;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record Blueprint<T>(
        Map<String, T> templates,
        Map<String, String> mappings,
        String moduleName,
        Path modulePath,
        QuephaestusConfiguration configuration,

        /**
         * Absolute path
         */
        Path workingDir,

        /**
         * Relative path to the working directory
         */
        Path baseDir,
        boolean interactive,
        Optional<List<String>> preForgeScript,
        Optional<List<String>> postForgeScript
) {
}

