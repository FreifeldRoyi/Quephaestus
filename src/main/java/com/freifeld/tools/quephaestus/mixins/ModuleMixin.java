package com.freifeld.tools.quephaestus.mixins;

import com.freifeld.tools.quephaestus.exceptions.AbsolutePathException;
import com.freifeld.tools.quephaestus.exceptions.ModuleNameIsEmptyException;
import jakarta.enterprise.context.ApplicationScoped;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Spec.Target;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@ApplicationScoped
public class ModuleMixin {
    private String moduleName;
    private Path modulePath;

    private CommandSpec spec;

    @Spec(Target.MIXEE)
    public void setCommandSpec(CommandSpec spec) {
        this.spec = spec;
    }

    @Option(names = {"-m", "--module"},
            description = "The name of the module to generate in",
            order = 3,
            required = true, // TODO TBD
            arity = "1")
    public void setModule(String module) throws IOException {
        if (module == null || module.isBlank()) {
            throw new ModuleNameIsEmptyException();
        }

        // transform a.b.c -> a/b/c
        var moduleFormatted = String.join(File.separator, module.split("\\."));
        var modulePath = Path.of(moduleFormatted);
        if (modulePath.isAbsolute()) {
            throw new AbsolutePathException(modulePath);
        }

        this.moduleName = modulePath.getFileName().toString();
        this.modulePath = modulePath;
    }

    public Path modulePath() {
        return this.modulePath;
    }

    public String moduleName() {
        return this.moduleName;
    }
}
