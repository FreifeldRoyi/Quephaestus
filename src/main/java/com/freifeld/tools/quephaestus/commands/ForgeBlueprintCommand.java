package com.freifeld.tools.quephaestus.commands;

import com.freifeld.tools.quephaestus.Blacksmith;
import com.freifeld.tools.quephaestus.configuration.Blueprint;
import com.freifeld.tools.quephaestus.mixins.*;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.freifeld.tools.quephaestus.messages.ExceptionMessageTemplates.invalidElementException;
import static com.freifeld.tools.quephaestus.messages.ExceptionMessageTemplates.templatesWereNotFound;
import static com.freifeld.tools.quephaestus.messages.SuccessMessageTemplates.forgeSuccessMessage;

@Command(name = "forge-blueprint", mixinStandardHelpOptions = true, sortOptions = true, sortSynopsis = true)
public class ForgeBlueprintCommand implements Runnable {
    @Mixin
    ConfigFileMixin configFileMixin;

    @Mixin
    ModuleMixin moduleMixin;

    @Mixin
    DirectoryMixin directoryMixin;

    @Mixin
    DataMixin dataMixin;

    @Mixin
    InteractiveModeMixin interactiveMixin;

    @Mixin
    ScriptsMixin scriptsMixin;

    @Spec
    CommandSpec commandSpec;

    @Inject
    Blacksmith blacksmith;

    private String blueprintName;

    @Parameters(paramLabel = "BLUEPRINT", index = "0", arity = "1")
    private void setBlueprintName(String blueprintName) {
        final var possibleKeys = this.configFileMixin.configuration().blueprints().keySet();
        if (!possibleKeys.contains(blueprintName)) {
            throw invalidElementException(
                    this.commandSpec,
                    blueprintName,
                    this.configFileMixin.configPath(),
                    possibleKeys);
        }
        this.blueprintName = blueprintName;
    }

    private Map<String, Path> findTemplateFiles() {
        final var blueprintDefinition = this.configFileMixin.configuration().blueprints().get(this.blueprintName);
        final var elements = blueprintDefinition.elements();
        final var templatePaths = elements.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        s -> this.configFileMixin.templatePath()
                                .resolve(s + ".qphs")));
        final var missing = templatePaths.values()
                .stream()
                .filter(path -> !Files.exists(path))
                .collect(Collectors.toSet());
        if (!missing.isEmpty()) {
            throw templatesWereNotFound(this.commandSpec, elements, missing);
        }

        return templatePaths;
    }

    public Map<String, String> createMappings() {
        // 1. blueprint mappings
        var blueprintMappings = this.configFileMixin.configuration().blueprints().get(this.blueprintName).mappings();

        // 2. data mappings
        var dataMappings = this.dataMixin.mappings();

        final var combined = Stream.concat(blueprintMappings.entrySet().stream(), dataMappings.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new HashMap<>(combined);
    }

    @Override
    public void run() {
        final var templatePaths = this.findTemplateFiles();
        final var configuration = this.configFileMixin.configuration();
        final var blueprint = new Blueprint(
                templatePaths,
                this.createMappings(),
                this.moduleMixin.moduleName(),
                this.moduleMixin.modulePath(),
                configuration,
                this.directoryMixin.workingDirectory(),
                this.directoryMixin.baseDirectory(),
                interactiveMixin.isInteractive(),
                this.scriptsMixin.preForge().or(() -> configuration.preForgeScript().map(ScriptsMixin::pathSplitter)),
                this.scriptsMixin.postForge().or(() -> configuration.postForgeScript().map(ScriptsMixin::pathSplitter));
        final var forgedFiles = this.blacksmith.forge(blueprint);
        forgeSuccessMessage(this.commandSpec, forgedFiles);
    }
}
