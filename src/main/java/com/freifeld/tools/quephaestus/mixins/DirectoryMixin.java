package com.freifeld.tools.quephaestus.mixins;

import com.freifeld.tools.quephaestus.exceptions.AbsolutePathException;
import com.freifeld.tools.quephaestus.exceptions.PathDoesNotExistException;
import jakarta.enterprise.context.ApplicationScoped;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@ApplicationScoped
public class DirectoryMixin {
    private Path baseDirectory;
    private Path workingDirectory;

    private CommandSpec commandSpec;

    @Spec
    public void setCommandSpec(CommandSpec commandSpec) {
        this.commandSpec = commandSpec;
    }

    @Option(names = {"-w", "--working-dir"}, description = "Working directory", order = 1, required = false, arity = "0..1")
    public void setWorkingDirectory(Optional<String> workingDirectory) {
        var directory = workingDirectory.or(() -> Optional.of("")).map(Path::of).map(p -> {
            if (!p.startsWith("~")) {
                return p;
            }
            var homeDir = System.getProperty("user.home");
            var truncated = p.subpath(1, p.getNameCount());
            return Path.of(homeDir).resolve(truncated);
        }).map(Path::toAbsolutePath).get();
        if (!Files.isDirectory(directory)) {
            throw new PathDoesNotExistException(directory);
        }

        this.workingDirectory = directory.normalize();
    }

    public Path workingDirectory() {
        return this.workingDirectory;
    }

    @Option(names = {"-b", "--base-dir"}, description = "base directory", order = 2, required = false, arity = "0..1")
    public void setBaseDirectory(Optional<String> baseDirectory) {
        var directory = baseDirectory.or(() -> Optional.of("")).map(Path::of).get();
        if (directory.isAbsolute()) {
            throw new AbsolutePathException(directory);
        }
        this.baseDirectory = directory.normalize();
    }

    public Path baseDirectory() {
        return this.baseDirectory;
    }
}
