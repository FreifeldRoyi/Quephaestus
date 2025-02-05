package com.freifeld.tools.quephaestus.commands;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine.Command;

@TopCommand
@Command(name = "quephaestus",
        mixinStandardHelpOptions = true,
        subcommands = {
                InitCommand.class,
                ListCommandsCommand.class,
                ForgeCommand.class,
                ForgeBlueprintCommand.class
        })
public class EntryCommand {
}
