package com.freifeld.tools.quephaestus.core;

import com.freifeld.tools.quephaestus.core.exceptions.UnhandledQuephaestusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystemTemplateResolver implements TemplateResolver<Path> {
    @Override
    public String resolve(Path templatePath) {
        try {
            return Files.readString(templatePath);
        } catch (IOException e) {
            throw new UnhandledQuephaestusException("Failed to parse template [%s]".formatted(templatePath), e);
        }
    }
}
