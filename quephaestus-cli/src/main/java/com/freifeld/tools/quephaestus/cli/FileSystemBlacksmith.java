package com.freifeld.tools.quephaestus.cli;

import com.freifeld.tools.quephaestus.core.*;
import com.freifeld.tools.quephaestus.core.configuration.Blueprint;
import com.freifeld.tools.quephaestus.core.exceptions.PathDoesNotExistException;
import com.freifeld.tools.quephaestus.core.exceptions.UnhandledQuephaestusException;
import com.freifeld.tools.quephaestus.core.scripting.ScriptRunner;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class FileSystemBlacksmith extends Blacksmith<Path> {
    private final ScriptRunner runner;
    private final InputStream input;
    private final PrintWriter output;

    @Inject
    public FileSystemBlacksmith(Forge forge, ScriptRunner runner, InputStream input, PrintWriter output) {
        super(forge, new FileSystemTemplateResolver());
        this.runner = runner;
        this.input = input;
        this.output = output;
    }

    @Override
    protected void collectMissingInterpolationSlots(Set<String> missingSlots, Map<String, String> datasource) {
        try (var inputDatasource = new InputStreamDatamapSource(this.input, slot -> this.output.println("%s?%n".formatted(slot)))) {
            for (var slot : missingSlots) {
                var value = inputDatasource.valueFor(slot);
                datasource.put(slot, value);
            }
        }
    }

    /**
     * Creates the final path to write: workingDir/baseDir/module/package/filename
     *
     * @return a fully constructed path including all parts:
     * e.g.</br>
     * root -> <code>/tmp</code></br>
     * module -> <code>my/module/path/modulename</code></br>
     * package -> <code>controllers/api/v1</code></br>
     * filename -> <code>MyController.java</code></br>
     * result ---> <code>/tmp/my/module/path/modulename/controllers/api/v1/MyController.java</code>
     */
    @Override
    protected Path prepareOutputPath(Path workingDir, Path baseDir, Path modulePath, String packagePath, String filename) {
        try {
            // Will always be absolute since it is absolute is transformed to absolute form
            final var root = workingDir.resolve(baseDir);
            if (!Files.isDirectory(root)) {
                throw new PathDoesNotExistException(root);
            }

            final var outputDirectory = root.resolve(modulePath).resolve(packagePath);
            Files.createDirectories(outputDirectory);
            return outputDirectory.resolve(filename);
        } catch (IOException e) {
            throw new UnhandledQuephaestusException("Failed to create output directory(ies)", e);
        }
    }

    @Override
    public void preForge(Blueprint<Path> blueprint) {
        blueprint.preForgeScript()
                .ifPresent(parts -> this.runner.scriptRunner(blueprint.workingDir(), parts));
    }


    @Override
    public void postForge(Blueprint<Path> blueprint, Map<Path, String> forged) {
        blueprint.postForgeScript()
                .ifPresent(parts -> this.runner.scriptRunner(blueprint.workingDir(), parts));
    }

    @Override
    protected void validateBlueprint(Blueprint<Path> blueprint) {
        super.validateBlueprint(blueprint);
        Validators.validateAbsolutePath(blueprint.workingDir());
        Validators.directoryExists(blueprint.workingDir());
        Validators.validateRelativePath(blueprint.baseDir());
        Validators.validateRelativePath(blueprint.modulePath());
    }
}
