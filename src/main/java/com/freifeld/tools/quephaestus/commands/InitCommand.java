package com.freifeld.tools.quephaestus.commands;

import com.freifeld.tools.quephaestus.Consts;
import com.freifeld.tools.quephaestus.exceptions.UnhandledQuephaestusException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Command(name = "init", mixinStandardHelpOptions = true, description = "Generates a base directory with sample configuration")
public class InitCommand implements Runnable {

    private static final String DEFAULT_OUTPUT_DIR = "." + Consts.APP_NAME;

    Path outputDirectory;

    @Option(names = {"-d", "--directory"}, description = "Output directory", defaultValue = DEFAULT_OUTPUT_DIR, order = 1, arity = "0..1")
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

        this.outputDirectory = directory.normalize();
    }


    @Override
    public void run() {
        try (final var configFile = this.getClass().getClassLoader().getResourceAsStream("init-sample/sample-configuration.yaml")) {
            // 0. Create directories if needed
            if (!Files.isDirectory(this.outputDirectory)) {
                Files.createDirectories(this.outputDirectory);
            }

            // 1. Copy configuration to dest
            Files.copy(configFile, this.outputDirectory.resolve(Consts.APP_NAME + ".yaml"));

            // 2. Create templates folder in dest
            Files.createDirectories(this.outputDirectory.resolve("templates"));
        } catch (IOException e) {
            throw new UnhandledQuephaestusException("Failed to copy the sample configuration file or generate the initial templates directory", e);
        }
    }
}
