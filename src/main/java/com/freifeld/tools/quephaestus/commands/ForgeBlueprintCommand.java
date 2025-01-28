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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.freifeld.tools.quephaestus.messages.ExceptionMessageTemplates.invalidParameterException;
import static com.freifeld.tools.quephaestus.messages.ExceptionMessageTemplates.templatesWereNotFound;
import static com.freifeld.tools.quephaestus.messages.SuccessMessageTemplates.forgeSuccessMessage;

@Command(name = "forge-blueprint", mixinStandardHelpOptions = true, sortOptions = true, sortSynopsis = true)
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

	public Map<String, String> blueprintMappings()
	{
		return new HashMap<>(this.configFileMixin.configuration()
		                                         .getBlueprints()
		                                         .get(this.blueprintName)
		                                         .getMappings());
	}

	@Override
	public void run()
	{
		// TODO validate blueprint includes that are also in commands
		final var templatePaths = this.findTemplateFiles();
		final var blueprint = new Blueprint(
				templatePaths,
				this.blueprintMappings(),
				this.moduleMixin.getModuleName(),
				this.moduleMixin.getModulePath(),
				this.configFileMixin.configuration(),
				this.directoryMixin.combined());
		final var forgedFiles = this.blacksmith.forge(blueprint);
		forgeSuccessMessage(this.commandSpec, forgedFiles);
	}
}
