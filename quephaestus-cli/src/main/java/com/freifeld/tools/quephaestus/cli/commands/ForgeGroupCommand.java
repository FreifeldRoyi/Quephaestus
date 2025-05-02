package com.freifeld.tools.quephaestus.cli.commands;

import com.freifeld.tools.quephaestus.cli.mixins.*;
import com.freifeld.tools.quephaestus.core.exceptions.InvalidParameterForCommandException;
import com.freifeld.tools.quephaestus.core.exceptions.TemplatesDoesNotExistException;
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

@Command(name = ForgeGroupCommand.COMMAND_NAME, mixinStandardHelpOptions = true, sortOptions = true, sortSynopsis = true)
public class ForgeGroupCommand extends AbstractForgeCommand {
    public static final String COMMAND_NAME = "forge-element-group";

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

    private String elementGroupName;

    @Parameters(paramLabel = "GROUP", index = "0", arity = "1")
    private void setElementGroupName(String elementGroupName) {
        final var possibleKeys = this.configFileMixin.configuration().elementGroups().keySet();
        if (!possibleKeys.contains(elementGroupName)) {
            throw new InvalidParameterForCommandException(
                    ForgeGroupCommand.COMMAND_NAME,
                    elementGroupName,
                    possibleKeys);
        }
        this.elementGroupName = elementGroupName;
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
        final var groups = this.configFileMixin.configuration().elementGroups().get(this.elementGroupName);
        final var elements = groups.elements();
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
        // 1. group mappings
        var groupMappings = this.configFileMixin.configuration().elementGroups().get(this.elementGroupName).mappings();

        // 2. data mappings
        var dataMappings = this.dataMixin.mappings();

        final var combined = Stream.concat(groupMappings.entrySet().stream(), dataMappings.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (_, dataMapping) -> dataMapping));
        return new HashMap<>(combined);
    }
}
