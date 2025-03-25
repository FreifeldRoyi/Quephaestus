package com.freifeld.tools.quephaestus.cli.commands;

import com.freifeld.tools.quephaestus.cli.mixins.ConfigFileMixin;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.util.Collection;

@Command(name = "list-elements", mixinStandardHelpOptions = true)
public class ListCommandsCommand implements Runnable {
    public static final String MESSAGE_FORMAT = "Possible element names are: %s";

    @Mixin
    ConfigFileMixin configFile;

    @Spec
    private CommandSpec spec;

    @Override
    public void run() {
        this.possibleElements(this.spec, this.configFile.configuration().elements().keySet());
    }

    public void possibleElements(CommandSpec spec, Collection<String> elements) {
        final var message = MESSAGE_FORMAT.formatted(elements);
        spec.commandLine().getOut().println(message);
    }
}
