package com.freifeld.tools.quephaestus.commands;

import com.freifeld.tools.quephaestus.Blacksmith;
import com.freifeld.tools.quephaestus.configuration.Blueprint;
import com.freifeld.tools.quephaestus.mixins.ConfigFileMixin;
import com.freifeld.tools.quephaestus.mixins.DirectoryMixin;
import com.freifeld.tools.quephaestus.mixins.ModuleMixin;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.freifeld.tools.quephaestus.messages.ExceptionMessageTemplates.invalidParameterException;
import static com.freifeld.tools.quephaestus.messages.ExceptionMessageTemplates.templateWasNotFound;
import static com.freifeld.tools.quephaestus.messages.SuccessMessageTemplates.forgeSuccessMessage;

@Command(name = "forge", mixinStandardHelpOptions = true, sortOptions = true, sortSynopsis = true)
public class ForgeCommand implements Runnable
{
	@Mixin
	ConfigFileMixin configFileMixin;

	@Mixin
	ModuleMixin moduleMixin;

	@Mixin
	DirectoryMixin directoryMixin;

	@Spec
	CommandSpec commandSpec;

	@Inject
	Blacksmith blacksmith;

	private String commandParameter;

	@Parameters(paramLabel = "CMD_PARAMETER", index = "0", arity = "1")
	private void setCommandParameter(String commandParameter)
	{
		final var possibleKeys = this.configFileMixin.configuration().getCommands().keySet();
		if (!possibleKeys.contains(commandParameter))
		{
			throw invalidParameterException(
					this.commandSpec,
					commandParameter,
					this.configFileMixin.configPath(),
					possibleKeys);
		}
		this.commandParameter = commandParameter;
	}

	private Path findTemplateFile()
	{
		final var templatePath = this.configFileMixin.templatePath().resolve(this.commandParameter + ".qphs");
		if (!Files.exists(templatePath))
		{
			throw templateWasNotFound(this.commandSpec, this.commandParameter);
		}

		return templatePath;
	}

	@Override
	public void run()
	{
		final var templatePath = this.findTemplateFile();
		final var blueprint = new Blueprint(
				templatePath,
				this.commandParameter,
				this.moduleMixin.getModuleName(),
				this.moduleMixin.getModulePath(),
				this.configFileMixin.configuration(),
				this.directoryMixin.combined());
		final var forgedFiles = this.blacksmith.forge(blueprint);
		forgeSuccessMessage(this.commandSpec, forgedFiles);
	}
}
