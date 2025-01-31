package com.freifeld.tools.quephaestus.mixins;

import com.freifeld.tools.quephaestus.configuration.ConfigReader;
import com.freifeld.tools.quephaestus.configuration.QuephaestusConfiguration;
import com.freifeld.tools.quephaestus.exceptions.ConfigurationValidationException;
import com.freifeld.tools.quephaestus.exceptions.TemplateFolderDoesNotExistException;
import com.freifeld.tools.quephaestus.exceptions.UnrecognizedElementsException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Validation;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class ConfigFileMixin {
    private QuephaestusConfiguration configuration;

    private Path configPath;
    private Path templatesPath;

    private CommandSpec commandSpec;

    @Spec
    public void setCommandSpec(CommandSpec spec) {
        this.commandSpec = spec;
    }

    @Option(names = {"-f", "--file-path"}, description = "Configuration file path", order = 0, required = false, arity = "0..1")
    private void setConfigFileOption(String fileName) {
        final var config = ConfigReader.readConfig(fileName);
        this.validateConfig(config, fileName);

        this.configuration = config; // TODO global file path should be handled as well
        this.setConfigPath(fileName);
        this.setTemplatesFolder();
    }

    private void validateConfig(QuephaestusConfiguration config, String configPath) throws ConfigurationValidationException {
        // 1. Simple Bean validation
        try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            final var validator = validatorFactory.getValidator();
            final var violations = validator.validate(config);

            if (!violations.isEmpty()) {
                throw new ConfigurationValidationException(violations, configPath);
            }
        }

        // 2. Validate that each blueprint's elements appear on the elementNames list
        final var elementNames = config.elements().keySet();
        final var missingElements = config.blueprints().entrySet().stream()
                .filter((definition) -> !elementNames.containsAll(definition.getValue().elements()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().elements().stream().filter(element -> !elementNames.contains(element)).collect(Collectors.toSet())
                ));
        if (!missingElements.isEmpty()) {
            throw new UnrecognizedElementsException(missingElements, configPath);
        }
    }

    private void setConfigPath(String fileName) {
        this.configPath = Path.of(fileName);
    }

    private void setTemplatesFolder() {
        final var templatesConfig = Path.of(this.configuration.templatesFolder());
        final var configPath = this.configPath.getParent();
        final var path = templatesConfig.isAbsolute() ? templatesConfig : configPath.resolve(templatesConfig);

        if (!Files.isDirectory(path)) {
            throw new TemplateFolderDoesNotExistException(path, configPath);
        }

        this.templatesPath = path;
    }

    public QuephaestusConfiguration configuration() {
        return this.configuration;
    }

    public Path configPath() {
        return this.configPath;
    }

    public Path templatePath() {
        return this.templatesPath;
    }
}
