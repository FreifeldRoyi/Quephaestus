package com.freifeld.tools.quephaestus.commands;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@TopCommand
@Command(name = "quephaestus",
         mixinStandardHelpOptions = true,
         subcommands = { ForgeCommand.class, ForgeBlueprintCommand.class, ListCommandsCommand.class })
public class EntryCommand
{
//	@Option(names = "-v")
//	public void setVerbose()
//	{
//		CommandLine.tracer().setLevel(CommandLine.TraceLevel.DEBUG); // TODO see that it actually works
//	}
}
