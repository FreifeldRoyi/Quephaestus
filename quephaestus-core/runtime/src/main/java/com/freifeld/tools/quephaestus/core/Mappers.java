package com.freifeld.tools.quephaestus.core;

import com.freifeld.tools.quephaestus.core.exceptions.ModuleNameIsEmptyException;

import java.io.File;
import java.nio.file.Path;

public class Mappers {

    public static Path prepareModulePath(String module) {
        if (module == null || module.isBlank()) { // TODO might change in the future
            throw new ModuleNameIsEmptyException();
        }

        // transform a.b.c -> a/b/c
        var moduleFormatted = String.join(File.separator, module.split("\\."));
        return Path.of(moduleFormatted);
    }

}
