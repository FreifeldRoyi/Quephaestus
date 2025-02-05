package com.freifeld.tools.quephaestus.commands;

import com.freifeld.tools.quephaestus.exceptions.InvalidParameterForCommandException;
import com.freifeld.tools.quephaestus.exceptions.TemplatesDoesNotExistException;
import com.freifeld.tools.quephaestus.mixins.*;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

@Command(name = ForgeCommand.COMMAND_NAME, mixinStandardHelpOptions = true, sortOptions = true, sortSynopsis = true)
public class ForgeCommand extends AbstractForgeCommand {
    public static final String COMMAND_NAME = "forge";

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

    private String element;

    @Parameters(paramLabel = "ELEMENT", index = "0", arity = "1")
    private void setElement(String element) {
        final var possibleKeys = this.configFileMixin.configuration().elements().keySet();
        if (!possibleKeys.contains(element)) {
            throw new InvalidParameterForCommandException(ForgeCommand.COMMAND_NAME, element, possibleKeys);
        }
        this.element = element;
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
        final var templatePath = this.configFileMixin.templatePath().resolve(this.element + ".qphs");
        if (!Files.exists(templatePath)) {
            throw new TemplatesDoesNotExistException(Collections.singleton(this.element), Collections.singleton(templatePath));
        }

        return Map.of(this.element, templatePath);
    }

    @Override
    protected Map<String, String> mappings() {
        return this.dataMixin.mappings();
    }
}
