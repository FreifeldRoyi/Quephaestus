package com.freifeld.tools.quephaestus.commands;

import com.freifeld.tools.quephaestus.mixins.ConfigFileMixin;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(name = "list-elements", mixinStandardHelpOptions = true)
public class ListCommandsCommand implements Runnable
{
	@Mixin
	ConfigFileMixin configFile;

	@Spec
	private CommandSpec spec;

	@Override
	public void run()
	{
		this.spec.commandLine()
		         .getOut()
		         .println("Possible element names are: " + this.configFile.configuration().getElements().keySet());
	}
}
