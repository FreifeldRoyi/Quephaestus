package com.freifeld.tools.quephaestus.commands;

import com.freifeld.tools.quephaestus.Consts;
import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine.Command;

@TopCommand
@Command(name = Consts.APP_NAME,
        mixinStandardHelpOptions = true,
        subcommands = {
                InitCommand.class,
                ListCommandsCommand.class,
                ForgeCommand.class,
                ForgeBlueprintCommand.class
        })
public class EntryCommand {
}
