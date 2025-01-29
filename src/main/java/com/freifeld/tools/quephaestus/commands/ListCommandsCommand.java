package com.freifeld.tools.quephaestus.commands;

import com.freifeld.tools.quephaestus.mixins.ConfigFileMixin;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import static com.freifeld.tools.quephaestus.messages.SuccessMessageTemplates.possibleElements;

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
		possibleElements(this.spec, this.configFile.configuration().elements().keySet());
	}
}
