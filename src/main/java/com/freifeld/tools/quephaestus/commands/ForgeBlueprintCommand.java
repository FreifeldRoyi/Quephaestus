package com.freifeld.tools.quephaestus.commands;

import com.freifeld.tools.quephaestus.exceptions.InvalidParameterForCommandException;
import com.freifeld.tools.quephaestus.exceptions.TemplatesDoesNotExistException;
import com.freifeld.tools.quephaestus.mixins.*;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Command(name = ForgeBlueprintCommand.COMMAND_NAME, mixinStandardHelpOptions = true, sortOptions = true, sortSynopsis = true)
public class ForgeBlueprintCommand extends AbstractForgeCommand {
    public static final String COMMAND_NAME = "forge-blueprint";

    @Mixin
    ConfigFileMixin configFileMixin;

    @Mixin
    ModuleMixin moduleMixin;

    @Mixin
    DirectoryMixin directoryMixin;

    @Mixin
    DataMixin dataMixin;

    @Mixin
    InteractiveModeMixin interactiveModeMixin;

    @Mixin
    ScriptsMixin scriptsMixin;

    private String blueprintName;

    @Parameters(paramLabel = "BLUEPRINT", index = "0", arity = "1")
    private void setBlueprintName(String blueprintName) {
        final var possibleKeys = this.configFileMixin.configuration().blueprints().keySet();
        if (!possibleKeys.contains(blueprintName)) {
            throw new InvalidParameterForCommandException(
                    ForgeBlueprintCommand.COMMAND_NAME,
                    blueprintName,
                    possibleKeys);
        }
        this.blueprintName = blueprintName;
    }

    @Override
    public ConfigFileMixin configFileMixin() {
        return this.configFileMixin;
    }

    @Override
    public ModuleMixin moduleMixin() {
        return this.moduleMixin;
    }

    @Override
    public DirectoryMixin directoryMixin() {
        return this.directoryMixin;
    }

    @Override
    public DataMixin dataMixin() {
        return this.dataMixin;
    }

    @Override
    public InteractiveModeMixin interactiveModeMixin() {
        return this.interactiveModeMixin;
    }

    @Override
    public ScriptsMixin scriptsMixin() {
        return this.scriptsMixin;
    }

    @Override
    protected Map<String, Path> findTemplateFiles() {
        final var blueprintDefinition = this.configFileMixin.configuration().blueprints().get(this.blueprintName);
        final var elements = blueprintDefinition.elements();
        final var templatePaths = elements.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        s -> this.configFileMixin.templatePath().resolve(s + ".qphs")));
        final var missing = templatePaths.values()
                .stream()
                .filter(path -> !Files.exists(path))
                .collect(Collectors.toSet());
        if (!missing.isEmpty()) {
            throw new TemplatesDoesNotExistException(elements, missing);
        }

        return templatePaths;
    }

    @Override
    protected Map<String, String> mappings() {
        // 1. blueprint mappings
        var blueprintMappings = this.configFileMixin.configuration().blueprints().get(this.blueprintName).mappings();

        // 2. data mappings
        var dataMappings = this.dataMixin.mappings();

        final var combined = Stream.concat(blueprintMappings.entrySet().stream(), dataMappings.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (_, dataMapping) -> dataMapping));
        return new HashMap<>(combined);
    }

}
