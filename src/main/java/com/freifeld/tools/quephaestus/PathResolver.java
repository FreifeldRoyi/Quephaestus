package com.freifeld.tools.quephaestus;

import com.freifeld.tools.quephaestus.exceptions.DirectoryDoesNotExistException;
import jakarta.enterprise.context.ApplicationScoped;

import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
public class PathResolver {

    public Path validatedResolvedDirectories(Path p1, Path p2, Path... paths) {
        return this.resolveDirectories(true, p1, p2, paths);
    }

    private Path resolveDirectories(boolean validate, Path p1, Path p2, Path... paths) {
        final var resolved = p1.resolve(p2, paths);
        if (validate && !Files.isDirectory(resolved)) {
            throw new DirectoryDoesNotExistException(resolved);
        }

        return resolved;
    }

}
