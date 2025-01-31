package com.freifeld.tools.quephaestus.commands;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine.Command;

@TopCommand
@Command(name = "quephaestus",
        mixinStandardHelpOptions = true,
        subcommands = {ForgeCommand.class, ForgeBlueprintCommand.class, ListCommandsCommand.class})
public class EntryCommand {
}
