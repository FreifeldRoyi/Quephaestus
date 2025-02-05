package com.freifeld.tools.quephaestus.commands;

import com.freifeld.tools.quephaestus.exceptions.PathDoesNotExistException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

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
                    var homeDir = System.getProperty("user.home");
                    var truncated = p.subpath(1, p.getNameCount());
                    return Path.of(homeDir).resolve(truncated);
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
        System.out.println("Running in " + this.outputDirectory);
    }
}
