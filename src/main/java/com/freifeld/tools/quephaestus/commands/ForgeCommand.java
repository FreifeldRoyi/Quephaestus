package com.freifeld.tools.quephaestus.commands;

import com.freifeld.tools.quephaestus.Blacksmith;
import com.freifeld.tools.quephaestus.configuration.Blueprint;
import com.freifeld.tools.quephaestus.mixins.ConfigFileMixin;
import com.freifeld.tools.quephaestus.mixins.DirectoryMixin;
import com.freifeld.tools.quephaestus.mixins.ModuleMixin;
import jakarta.inject.Inject;
import picocli.CommandLine;
import picocli.CommandLine.*;
import picocli.CommandLine.Model.CommandSpec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static com.freifeld.tools.quephaestus.exceptions.ExceptionMessageTemplates.INVALID_PARAMETER;

@Command(name = "forge",
         mixinStandardHelpOptions = true,
         scope = CommandLine.ScopeType.INHERIT,
         sortOptions = true,
         sortSynopsis = true)
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
			throw this.invalidCommandParameterException(commandParameter, possibleKeys);
		}
		this.commandParameter = commandParameter;
	}

	public ParameterException invalidCommandParameterException(String parameter, Set<String> possibleKeys)
	{
		final var message = INVALID_PARAMETER.formatted(parameter, possibleKeys, this.configFileMixin.configPath());
		return new ParameterException(this.commandSpec.commandLine(), message);
	}


	private Path findTemplateFile()
	{
		try (var fileStream = Files.list((this.configFileMixin.templatePath())))
		{
			var maybeFile = fileStream.filter(path -> path.getFileName()
			                                              .toString()
			                                              .startsWith(this.commandParameter + "."))
			                          .findFirst(); // TODO better solution for path matching
			return maybeFile.orElseThrow(() -> new RuntimeException("Template was not found")); // TODO better message + better exception
		}
		catch (IOException e)
		{
			throw new RuntimeException(e); // TODO
		}
	}

	@Override
	public void run()
	{
		final var templateFile = this.findTemplateFile();
		var blueprint = new Blueprint(
				templateFile,
				this.commandParameter,
				this.moduleMixin.getModuleName(),
				this.moduleMixin.getModulePath(),
				this.configFileMixin.configuration(),
				this.directoryMixin.combined());
		this.blacksmith.forgeOnce(blueprint);

		// TODO printout done ?
		System.out.println("DONE. created files are...");
	}


}
