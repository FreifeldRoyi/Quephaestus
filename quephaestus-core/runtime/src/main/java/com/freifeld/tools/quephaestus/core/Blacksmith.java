package com.freifeld.tools.quephaestus.core;

import com.freifeld.tools.quephaestus.core.configuration.Blueprint;
import com.freifeld.tools.quephaestus.core.configuration.Element;
import com.freifeld.tools.quephaestus.core.exceptions.MissingDataException;
import io.smallrye.mutiny.tuples.Tuple2;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Blacksmith<T> {
    protected Forge forge;
    protected TemplateResolver<T> templateResolver;

    public Blacksmith() {
    }

    public Blacksmith(Forge forge, TemplateResolver<T> templateResolver) {
        this.forge = forge;
        this.templateResolver = templateResolver;
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
    abstract protected Path prepareOutputPath(Path workingDir, Path baseDir, Path modulePath, String packagePath, String filename);

    private Map<String, String> initialMappings(Blueprint<T> blueprint) {
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
                            return this.forge.interpolationSlotsFrom(mappingTemplate)
                                    .findAny()
                                    .map(_ -> this.forge.render(mappingTemplate, datasource))
                                    .orElse(entry.getValue());
                        },
                        (_, s2) -> s2
                )
        );
        datasource.putAll(externalMappings);

        return datasource;
    }

    public final Stream<String> interpolationSlotsFrom(ForgeMaterial material) throws MissingDataException {
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

    public final ForgeMaterial prepareForgeMaterial(String elementName, Element element, String templateContent) {
        final var template = this.forge.parse(templateContent);
        final var filenameTemplate = this.forge.parse(element.namePattern());
        final var elementPathTemplate = this.forge.parse(element.path());

        return new ForgeMaterial(elementName, template, filenameTemplate, elementPathTemplate);
    }

    protected void collectMissingInterpolationSlots(Set<String> missingSlots, Map<String, String> datasource) {
        // Intentionally empty
    }

    private Map<Path, String> doForge(Blueprint<T> blueprint) {
        // 1. Collect materials for forging
        final var elements = blueprint.configuration().elements();
        final var materials = blueprint.templates()
                .entrySet()
                .stream()
                .map(entry -> {
                    final var element = entry.getKey();
                    final var template = this.templateResolver.resolve(entry.getValue());
                    return this.prepareForgeMaterial(element, elements.get(element), template);
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

            this.collectMissingInterpolationSlots(missingSlots, datasource);
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

        return filesToWrite;
    }

    protected void preForge(Blueprint<T> blueprint) {
        // Intentionally empty
    }

    protected void postForge(Blueprint<T> blueprint, Map<Path, String> forged) {
        // Intentionally empty
    }

    public final Map<Path, String> forge(Blueprint<T> blueprint) {
        this.validateBlueprint(blueprint);
        this.preForge(blueprint);
        final var forged = this.doForge(blueprint);
        this.postForge(blueprint, forged);
        return forged;
    }

    protected void validateBlueprint(Blueprint<T> blueprint) {
        Validators.validateConfiguration(blueprint.configuration());
    }
}
