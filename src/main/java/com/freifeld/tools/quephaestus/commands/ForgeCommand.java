package com.freifeld.tools.quephaestus.commands;

import com.freifeld.tools.quephaestus.Blacksmith;
import com.freifeld.tools.quephaestus.configuration.Blueprint;
import com.freifeld.tools.quephaestus.exceptions.MissingDataException;
import com.freifeld.tools.quephaestus.mixins.ConfigFileMixin;
import com.freifeld.tools.quephaestus.mixins.DataMixin;
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

import static com.freifeld.tools.quephaestus.messages.ExceptionMessageTemplates.*;
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

	@Mixin
	DataMixin dataMixin;

	@Spec
	CommandSpec commandSpec;

	@Inject
	Blacksmith blacksmith;

	private String element;

	@Parameters(paramLabel = "ELEMENT", index = "0", arity = "1")
	private void setElement(String element)
	{
		final var possibleKeys = this.configFileMixin.configuration().getElements().keySet();
		if (!possibleKeys.contains(element))
		{
			throw invalidElementException(this.commandSpec, element, this.configFileMixin.configPath(), possibleKeys);
		}
		this.element = element;
	}

	private Path findTemplateFile()
	{
		final var templatePath = this.configFileMixin.templatePath().resolve(this.element + ".qphs");
		if (!Files.exists(templatePath))
		{
			throw templateWasNotFound(this.commandSpec, this.element);
		}

		return templatePath;
	}

	@Override
	public void run()
	{
		final var templatePath = this.findTemplateFile();
		final var blueprint = new Blueprint(
				templatePath,
				this.element,
				this.dataMixin.getMappings(),
				this.moduleMixin.getModuleName(),
				this.moduleMixin.getModulePath(),
				this.configFileMixin.configuration(),
				this.directoryMixin.combined());

		try
		{
			final var forgedFiles = this.blacksmith.forge(blueprint);
			forgeSuccessMessage(this.commandSpec, forgedFiles);
		}
		catch (MissingDataException e)
		{
			throw interpolationSlotsMissingValues(this.commandSpec, e.missingData());
		}
	}
}
