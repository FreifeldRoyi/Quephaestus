package com.freifeld.tools.quephaestus.core;

import com.freifeld.tools.quephaestus.core.configuration.Blueprint;
import com.freifeld.tools.quephaestus.core.configuration.Element;
import com.freifeld.tools.quephaestus.core.exceptions.MissingDataException;
import com.freifeld.tools.quephaestus.core.exceptions.PathDoesNotExistException;
import com.freifeld.tools.quephaestus.core.exceptions.UnhandledQuephaestusException;
import com.freifeld.tools.quephaestus.core.scripting.ScriptRunner;
import io.smallrye.mutiny.tuples.Tuple2;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class Blacksmith {
    private final Forge forge;
    private final ScriptRunner runner;
    private final FileSystemWriter fileWriter;
    private final InputStream input;
    private final PrintWriter output;

    @Inject
    public Blacksmith(Forge forge, ScriptRunner runner, FileSystemWriter fileWriter, InputStream input, PrintWriter output) {
        this.forge = forge;
        this.runner = runner;
        this.fileWriter = fileWriter;
        this.input = input;
        this.output = output;
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
    private Path prepareOutputPath(Path workingDir, Path baseDir, Path modulePath, String packagePath, String filename) {
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

    private Map<String, String> initialMappings(Blueprint blueprint) {
        final var datasource = new HashMap<String, String>();
        // 1. Configuration
        blueprint.configuration().project().ifPresent(project -> datasource.put("project", project));
        blueprint.configuration().namespace().ifPresent(namespace -> datasource.put("namespace", namespace));
        datasource.put("module", blueprint.moduleName()); // TODO should be optional

        // 2. External mappings - can be static or set from well known interpolation slots
        final var externalMappings = blueprint.mappings().entrySet().stream().collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            final var mappingTemplate = this.forge.parse(entry.getValue());
                            final var value = this.forge.interpolationSlotsFrom(mappingTemplate)
                                    .findAny()
                                    .map(_ -> this.forge.render(mappingTemplate, datasource))
                                    .orElse(entry.getValue());
                            return value;
                        },
                        (s, s2) -> s2
                )
        );
        datasource.putAll(externalMappings);

        return datasource;
    }

    public Stream<String> interpolationSlotsFrom(ForgeMaterial material) throws MissingDataException {
        // 1. Template
        final var templateInterpolationSlots = this.forge.interpolationSlotsFrom(material.template());

        // 2. filenameTemplate
        final var filenameInterpolationSlots = this.forge.interpolationSlotsFrom(material.filename());

        // 3. elementPath
        final var elementPathInterpolationSlots = this.forge.interpolationSlotsFrom(material.elementPath());

        // all
        return Stream
                .of(templateInterpolationSlots, filenameInterpolationSlots, elementPathInterpolationSlots)
                .flatMap(Function.identity())
                .distinct();
    }

    public ForgeMaterial prepareForgeMaterial(String elementName, Element element, Path templatePath) {
        final var template = this.forge.parse(templatePath);
        final var filenameTemplate = this.forge.parse(element.namePattern());
        final var elementPathTemplate = this.forge.parse(element.path());

        return new ForgeMaterial(elementName, template, filenameTemplate, elementPathTemplate);
    }

    private void collectInterpolationSlotsInteractively(Set<String> missingSlots, Map<String, String> datasource) {
        try (var inputDatasource = new InputStreamDatamapSource(this.input, slot -> this.output.println("%s?%n".formatted(slot)))) {
            for (var slot : missingSlots) {
                var value = inputDatasource.valueFor(slot);
                datasource.put(slot, value);
            }
        }
    }

    public Set<Path> forge(Blueprint blueprint) {
        this.validateBlueprint(blueprint);

        // preScript
        blueprint.preForgeScript()
                .ifPresent(parts -> this.runner.scriptRunner(blueprint.workingDir(), parts));

        // 1. Collect materials for forging
        final var elements = blueprint.configuration().elements();
        final var materials = blueprint.templatePaths()
                .entrySet()
                .stream()
                .map(entry -> {
                    final var element = entry.getKey();
                    return this.prepareForgeMaterial(element, elements.get(element), entry.getValue());
                })
                .toList();

        // 2. Make sure all interpolation points have been filled
        final var datasource = this.initialMappings(blueprint);
        final var missingSlots = materials.stream()
                .flatMap(this::interpolationSlotsFrom)
                .filter(slot -> !datasource.containsKey(slot))
                .collect(Collectors.toSet());

        if (!datasource.keySet().containsAll(missingSlots)) {
            if (!blueprint.interactive()) {
                throw new MissingDataException(missingSlots);
            }

            this.collectInterpolationSlotsInteractively(missingSlots, datasource);
        }

        // 3. Render
        final var filesToWrite = materials.stream()
                .map(material -> {
                    var fileToWrite = this.forge.render(material.template(), datasource);
                    var filenameRendered = this.forge.render(material.filename(), datasource);
                    var elementPathRendered = this.forge.render(material.elementPath(), datasource);
                    var path = this.prepareOutputPath(
                            blueprint.workingDir(),
                            blueprint.baseDir(),
                            blueprint.modulePath(),
                            elementPathRendered,
                            filenameRendered);
                    return Tuple2.of(path, fileToWrite);
                })
                .collect(Collectors.toMap(Tuple2::getItem1, Tuple2::getItem2));

        // 4. Write
        for (var fileEntry : filesToWrite.entrySet()) {
            try {
                this.fileWriter.writeContent(fileEntry.getKey(), fileEntry.getValue());
            } catch (IOException e) {
                throw new UnhandledQuephaestusException("Failed to write files", e);
            }
        }

        // postScript
        blueprint.postForgeScript()
                .ifPresent(parts -> this.runner.scriptRunner(blueprint.workingDir(), parts));

        return filesToWrite.keySet();
    }

    private void validateBlueprint(Blueprint blueprint) {
        // 1.1 working directory is absolute (DirectoryMixing.java)
        Validators.validateAbsolutePath(blueprint.workingDir());
        // 1.2 working directory path exists and it is a directory
        Validators.directoryExists(blueprint.workingDir());

        // 2. base dir is not absolute (DirectoryMixin.java)
        Validators.validateRelativePath(blueprint.baseDir());

        // 3. module is not absolute (ModuleMixin.java & Mappers.java)
        Validators.validateRelativePath(blueprint.modulePath());

        // 4. validate configuration (ConfigFileMixin.java)
        Validators.validateConfiguration(blueprint.configuration());
    }
}
