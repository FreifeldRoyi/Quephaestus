package com.freifeld.tools.quephaestus;

import com.freifeld.tools.quephaestus.exceptions.PathDoesNotExistException;

import java.nio.file.Files;
import java.nio.file.Path;

public class PathResolver {

    public static Path validatedResolvedDirectories(Path p1, Path p2, Path... paths) {
        return resolveDirectories(true, p1, p2, paths);
    }

    private static Path resolveDirectories(boolean validate, Path p1, Path p2, Path... paths) throws PathDoesNotExistException {
        final var resolved = p1.resolve(p2, paths);
        if (validate && !Files.isDirectory(resolved)) {
            throw new PathDoesNotExistException(resolved);
        }

        return resolved;
    }

    public static Path existingPathFrom(String stringedPath) throws PathDoesNotExistException {
        final var path = Path.of(stringedPath);
        if (!Files.exists(path)) {
            throw new PathDoesNotExistException(path);
        }

        return path;
    }

}
