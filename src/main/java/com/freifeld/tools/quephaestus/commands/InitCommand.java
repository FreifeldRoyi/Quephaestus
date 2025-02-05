package com.freifeld.tools.quephaestus.commands;

import com.freifeld.tools.quephaestus.exceptions.PathDoesNotExistException;
import com.freifeld.tools.quephaestus.exceptions.UnhandledQuephaestusException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Command(name = "init", mixinStandardHelpOptions = true)
public class InitCommand implements Runnable {

    Path outputDirectory;

    @Option(names = {"-d", "--directory"}, description = "Output directory", defaultValue = ".", order = 1, required = false, arity = "0..1")
    public void setDirectory(String outputDirectory) {
        final var directory = Optional.ofNullable(outputDirectory)
                .filter(s -> !s.isBlank())
                .or(() -> Optional.of("."))
                .map(Path::of)
                .map(p -> {
                    if (!p.startsWith("~")) {
                        return p;
                    }
                    var home = Path.of(System.getProperty("user.home"));
                    return p.getNameCount() > 1 ? home.resolve(p.subpath(1, p.getNameCount())) : home;
                })
                .map(Path::toAbsolutePath)
                .get();

        if (!Files.isDirectory(directory)) {
            throw new PathDoesNotExistException(directory);
        }

        this.outputDirectory = directory;
    }


    @Override
    public void run() {
        try (final var configFile = this.getClass().getClassLoader().getResourceAsStream("init-sample/sample-configuration.yaml")) {
            // 1. Copy configuration to dest
            Files.copy(configFile, this.outputDirectory.resolve("configuration.yaml"));

            // 2. Create templates folder in dest
            Files.createDirectories(this.outputDirectory.resolve("templates"));
        } catch (IOException e) {
            throw new UnhandledQuephaestusException("Failed to copy the sample configuration file or generate the initial templates directory", e);
        }
    }
}
