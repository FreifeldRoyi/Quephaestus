package com.freifeld.tools.quephaestus.cli.mixins;

import com.freifeld.tools.quephaestus.core.configuration.ConfigReader;
import com.freifeld.tools.quephaestus.core.configuration.QuephaestusConfiguration;
import com.freifeld.tools.quephaestus.core.exceptions.ConfigurationValidationException;
import com.freifeld.tools.quephaestus.core.exceptions.TemplateFolderDoesNotExistException;
import com.freifeld.tools.quephaestus.core.exceptions.UnrecognizedElementsException;
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

    @Option(names = {"-f", "--file-path"}, description = "Configuration file path", order = 0, required = false, arity = "0..1")
    private void setConfigFileOption(String fileName) {
        this.configuration = ConfigReader.readConfig(fileName); // TODO global file path should be handled as well
        this.setConfigPath(fileName);
        this.setTemplatesFolder();
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
