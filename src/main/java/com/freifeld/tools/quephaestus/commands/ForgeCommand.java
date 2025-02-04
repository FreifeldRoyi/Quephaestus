package com.freifeld.tools.quephaestus.commands;

import com.freifeld.tools.quephaestus.Blacksmith;
import com.freifeld.tools.quephaestus.configuration.Blueprint;
import com.freifeld.tools.quephaestus.configuration.QuephaestusConfiguration;
import com.freifeld.tools.quephaestus.mixins.*;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import static com.freifeld.tools.quephaestus.messages.ExceptionMessageTemplates.invalidElementException;
import static com.freifeld.tools.quephaestus.messages.ExceptionMessageTemplates.templateWasNotFound;
import static com.freifeld.tools.quephaestus.messages.SuccessMessageTemplates.forgeSuccessMessage;

@Command(name = "forge", mixinStandardHelpOptions = true, sortOptions = true, sortSynopsis = true)
public class ForgeCommand implements Runnable {
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

    Blacksmith blacksmith;

    private String element;

    @Inject
    public void setBlacksmith(Blacksmith blacksmith) {
        this.blacksmith = blacksmith;
    }

    @Parameters(paramLabel = "ELEMENT", index = "0", arity = "1")
    private void setElement(String element) {
        final var possibleKeys = this.configFileMixin.configuration().elements().keySet();
        if (!possibleKeys.contains(element)) {
            throw invalidElementException(this.commandSpec, element, this.configFileMixin.configPath(), possibleKeys);
        }
        this.element = element;
    }

    private Path findTemplateFile() {
        final var templatePath = this.configFileMixin.templatePath().resolve(this.element + ".qphs");
        if (!Files.exists(templatePath)) {
            throw templateWasNotFound(this.commandSpec, this.element);
        }

        return templatePath;
    }

    @Override
    public void run() {
        final var templatePath = this.findTemplateFile();

        final var configuration = this.configFileMixin.configuration();
        final var blueprint = new Blueprint(
                Map.of(this.element, templatePath),
                this.dataMixin.mappings(),
                this.moduleMixin.moduleName(),
                this.moduleMixin.modulePath(),
                configuration,
                this.directoryMixin.workingDirectory(),
                this.directoryMixin.baseDirectory(),
                this.interactiveMixin.isInteractive(),
                this.scriptsMixin.preForge().or(() -> configuration.preForgeScript().map(ScriptsMixin::pathSplitter)),
                this.scriptsMixin.postForge().or(() -> configuration.postForgeScript().map(ScriptsMixin::pathSplitter))
        );


        final var forgedFiles = this.blacksmith.forge(blueprint);
        forgeSuccessMessage(this.commandSpec, forgedFiles);
    }
}
