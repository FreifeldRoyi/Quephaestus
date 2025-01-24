package com.freifeld.tools.quephaestus.commands;

import com.freifeld.tools.quephaestus.BlacksmithForge;
import com.freifeld.tools.quephaestus.InputStreamDatamapSource;
import com.freifeld.tools.quephaestus.mixins.ConfigFileMixin;
import com.freifeld.tools.quephaestus.mixins.ModuleMixin;
import jakarta.inject.Inject;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.freifeld.tools.quephaestus.exceptions.ExceptionMessageTemplates.INVALID_PARAMETER;

@Command(name = "forge", mixinStandardHelpOptions = true, scope = CommandLine.ScopeType.INHERIT)
public class ForgeCommand implements Runnable
{
	@Mixin
	ConfigFileMixin configFile;

	@Mixin
	ModuleMixin module;

	@Spec
	CommandSpec spec;

	@Inject
	BlacksmithForge forge;

	private String commandParameter;

	@Parameters(paramLabel = "CMD_PARAMETER", index = "0", arity = "1")
	private void setCommandParameter(String commandParameter)
	{
		final var possibleKeys = this.configFile.configuration().getCommands().keySet();
		if (!possibleKeys.contains(commandParameter))
		{
			throw this.invalidCommandParameterException(commandParameter, possibleKeys);
		}
		this.commandParameter = commandParameter;
	}

	public CommandLine.ParameterException invalidCommandParameterException(String parameter, Set<String> possibleKeys)
	{
		final var message = INVALID_PARAMETER.formatted(parameter, possibleKeys, this.configFile.configPath());
		return new CommandLine.ParameterException(this.spec.commandLine(), message);
	}


	private Path findTemplateFile()
	{
		try (var fileStream = Files.list((this.configFile.templatePath())))
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

	private Map<String, String> initialDatamap()
	{
		final var configuration = this.configFile.configuration();
		final var datamap = new HashMap<String, String>();
		datamap.put("namespace", configuration.getNamespace());
		datamap.put("projectName", configuration.getProjectName());
		return datamap;
	}

	private Path outputPath()
	{
		return null;
		//		this.configFile.
	}

	@Override
	public void run()
	{
		final var templateFile = this.findTemplateFile();
		final var renderedTemplate = this.forge.forgeOne(
				templateFile, new InputStreamDatamapSource(
						this.initialDatamap(),
						System.in,
						name -> this.spec.commandLine().getOut().println(name + "?")));
		System.out.println(renderedTemplate);
	}


}
