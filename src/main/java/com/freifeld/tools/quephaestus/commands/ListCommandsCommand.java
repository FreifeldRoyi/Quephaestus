package com.freifeld.tools.quephaestus.commands;

import com.freifeld.tools.quephaestus.mixins.ConfigFileMixin;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(name = "list-commands", mixinStandardHelpOptions = true, scope = CommandLine.ScopeType.INHERIT)
public class ListCommandsCommand implements Runnable
{
	@CommandLine.Mixin
	ConfigFileMixin configFile;

	@Spec
	private CommandSpec spec;

	@Override
	public void run()
	{
		this.spec.commandLine()
		         .getOut()
		         .println("Possible command names are: " + this.configFile.configuration().getCommands().keySet());
	}
}
