package com.freifeld.tools.quephaestus.cli.commands;

import com.freifeld.tools.quephaestus.core.Consts;
import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine.Command;

@TopCommand
@Command(name = Consts.APP_NAME,
        mixinStandardHelpOptions = true,
        subcommands = {
                InitCommand.class,
                ListCommandsCommand.class,
                ForgeCommand.class,
                ForgeGroupCommand.class
        })
public class EntryCommand {
}
