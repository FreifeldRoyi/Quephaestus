package com.freifeld.tools.quephaestus.cli.mixins;

import com.freifeld.tools.quephaestus.core.Mappers;
import jakarta.enterprise.context.ApplicationScoped;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Path;

@ApplicationScoped
public class ModuleMixin {
    private String moduleName;
    private Path modulePath;

    @Option(names = {"-m", "--module"},
            description = "The name of the module to generate in",
            order = 3,
            required = true, // TODO TBD
            arity = "1")
    public void setModule(String module) throws IOException {
        this.modulePath = Mappers.prepareModulePath(module);
        this.moduleName = this.modulePath.getFileName().toString();
    }

    public Path modulePath() {
        return this.modulePath;
    }

    public String moduleName() {
        return this.moduleName;
    }
}
