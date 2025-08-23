package com.freifeld.tools.quephaestus.cli.commands;

import com.freifeld.tools.quephaestus.cli.mixins.*;
import com.freifeld.tools.quephaestus.cli.FileSystemBlacksmith;
import com.freifeld.tools.quephaestus.core.configuration.Blueprint;
import com.freifeld.tools.quephaestus.core.exceptions.TemplatesDoesNotExistException;
import com.freifeld.tools.quephaestus.core.exceptions.UnhandledQuephaestusException;
import jakarta.inject.Inject;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static picocli.CommandLine.Help.Ansi.AUTO;

public abstract class AbstractForgeCommand implements Runnable {
    public static final String MESSAGE_FORMAT = AUTO.string("""
            \uD83C\uDF89 @|bold,fg(green) DONE|@ \uD83C\uDF89
            Created:
            %s
            """.strip());

    protected CommandSpec commandSpec;
    protected FileSystemBlacksmith blacksmith;

    @Inject
    public void setBlacksmith(FileSystemBlacksmith blacksmith) {
        this.blacksmith = blacksmith;
    }

    @Spec
    public void commandSpec(CommandSpec commandSpec) {
        this.commandSpec = commandSpec;
    }

    public abstract ConfigFileMixin configFileMixin();

    public abstract ModuleMixin moduleMixin();

    public abstract DirectoryMixin directoryMixin();

    public abstract DataMixin dataMixin();

    public abstract InteractiveModeMixin interactiveModeMixin();

    public abstract ScriptsMixin scriptsMixin();

    protected abstract Map<String, Path> findTemplateFiles() throws TemplatesDoesNotExistException;

    protected abstract Map<String, String> mappings();

    @Override
    public void run() {
        final var paths = this.findTemplateFiles();

        final var configuration = this.configFileMixin().configuration();
        final var blueprint = new Blueprint<>(
                paths,
                this.mappings(),
                this.moduleMixin().moduleName(),
                this.moduleMixin().modulePath(),
                configuration,
                this.directoryMixin().workingDirectory(),
                this.directoryMixin().baseDirectory(),
                this.interactiveModeMixin().isInteractive(),
                this.scriptsMixin().preForge().or(() -> configuration.preForgeScript().map(ScriptsMixin::pathSplitter)),
                this.scriptsMixin().postForge().or(() -> configuration.postForgeScript().map(ScriptsMixin::pathSplitter))
        );


        final var forgedFiles = this.blacksmith.forge(blueprint);
        this.writeFiles(forgedFiles);
        this.forgeSuccessMessage(this.commandSpec, forgedFiles.keySet());

    }

    private void writeFiles(Map<Path, String> forgedFiles) {
        for (var fileEntry : forgedFiles.entrySet()) {
            try {
                Files.writeString(fileEntry.getKey(), fileEntry.getValue());
            } catch (IOException e) {
                throw new UnhandledQuephaestusException("Failed to write files", e);
            }
        }
    }

    public void forgeSuccessMessage(CommandSpec spec, Collection<Path> paths) {
        final var createdPathsFormatted = paths.stream().map(p -> " - " + p).collect(Collectors.joining("\n"));
        final var message = MESSAGE_FORMAT.formatted(createdPathsFormatted);
        spec.commandLine().getOut().println(message);
    }
}
