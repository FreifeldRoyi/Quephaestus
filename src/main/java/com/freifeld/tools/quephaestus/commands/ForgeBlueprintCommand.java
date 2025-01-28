package com.freifeld.tools.quephaestus.commands;

import com.freifeld.tools.quephaestus.Blacksmith;
import com.freifeld.tools.quephaestus.configuration.Blueprint;
import com.freifeld.tools.quephaestus.mixins.ConfigFileMixin;
import com.freifeld.tools.quephaestus.mixins.DirectoryMixin;
import com.freifeld.tools.quephaestus.mixins.ModuleMixin;
import jakarta.inject.Inject;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.freifeld.tools.quephaestus.exceptions.ExceptionMessageTemplates.invalidParameterException;
import static com.freifeld.tools.quephaestus.exceptions.ExceptionMessageTemplates.templatesWereNotFound;
import static picocli.CommandLine.Help.Ansi.AUTO;

@Command(name = "forge-blueprint",
         mixinStandardHelpOptions = true,
         sortOptions = true,
         sortSynopsis = true)
public class ForgeBlueprintCommand implements Runnable
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

	private String blueprintName;

	@Parameters(paramLabel = "BLUEPRINT", index = "0", arity = "1")
	private void setBlueprintName(String blueprintName)
	{
		final var possibleKeys = this.configFileMixin.configuration().getBlueprints().keySet();
		if (!possibleKeys.contains(blueprintName))
		{
			throw invalidParameterException(
					this.commandSpec,
					blueprintName,
					this.configFileMixin.configPath(),
					possibleKeys);
		}
		this.blueprintName = blueprintName;
	}

	private Map<String, Path> findTemplateFiles()
	{
		final var blueprintDefinition = this.configFileMixin.configuration().getBlueprints().get(this.blueprintName);
		final var parameters = blueprintDefinition.getParameters();
		final var templatePaths = parameters.stream()
		                                    .collect(Collectors.toMap(
				                                    Function.identity(),
				                                    s -> this.configFileMixin.templatePath()
				                                                             .resolve(s + ".qphs")));
		final var missing = templatePaths.values()
		                                 .stream()
		                                 .filter(path -> !Files.exists(path))
		                                 .collect(Collectors.toSet());
		if (!missing.isEmpty())
		{
			throw templatesWereNotFound(this.commandSpec, parameters, missing);
		}

		return templatePaths;
	}

	@Override
	public void run()
	{
		// TODO validate blueprint includes that are also in commands

		final var templatePaths = this.findTemplateFiles();
		final var blueprint = new Blueprint(
				templatePaths,
				this.moduleMixin.getModuleName(),
				this.moduleMixin.getModulePath(),
				this.configFileMixin.configuration(),
				this.directoryMixin.combined());
		final var forgedFiles = this.blacksmith.forge(blueprint);

		final var createdFiles = forgedFiles.stream().map(p -> " - " + p).collect(Collectors.joining("\n"));
		final var successMessage = AUTO.string("""
		                           \uD83C\uDF89 @|bold,fg(green) DONE|@ \uD83C\uDF89
		                           Created:
		                           %s
		                           """.trim().formatted(createdFiles));
		this.commandSpec.commandLine().getOut().println(successMessage);
	}
}
