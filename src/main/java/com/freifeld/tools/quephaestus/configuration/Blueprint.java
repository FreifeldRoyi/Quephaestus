package com.freifeld.tools.quephaestus.configuration;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record Blueprint(
        Map<String, Path> templatePaths,
        Map<String, String> mappings,
        String moduleName,
        Path modulePath,
        QuephaestusConfiguration configuration,
        Path workingDir,
        Path baseDir,
        boolean interactive,
        Optional<List<String>> preForgeScript,
        Optional<List<String>> postForgeScript
) {
}
