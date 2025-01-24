package com.freifeld.tools.quephaestus.mixins;

import jakarta.enterprise.context.ApplicationScoped;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Spec.Target;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.freifeld.tools.quephaestus.exceptions.ExceptionMessageTemplates.MODULE_NOT_DEFINED;
import static com.freifeld.tools.quephaestus.exceptions.ExceptionMessageTemplates.PATH_ALREADY_EXISTS;

@ApplicationScoped
public class ModuleMixin
{
	private String modulePath;

	private CommandSpec spec;

	@Spec(Target.MIXEE)
	public void setSpec(CommandSpec spec)
	{
		this.spec = spec;
	}

	@CommandLine.Option(names = { "-m", "--module" },
	                    description = "The name of the module to generate in",
	                    required = true)
	public void setModule(String module) throws IOException
	{
		if (module == null || module.isBlank())
		{
			throw this.moduleNotFoundException();
		}

		// transform a.b.c -> a/b/c
		var modulePath = String.join(File.pathSeparator, module.split("\\."));
		var path = Path.of(modulePath);

		if (Files.exists(path) && !Files.isDirectory(path))
		{
			throw this.pathAlreadyExistsException(path);
		}
		if (Files.isDirectory(path))
		{
			try (var files = Files.newDirectoryStream(path))
			{
				if (files.iterator().hasNext())
				{
					throw this.pathAlreadyExistsException(path);
				}
			}
		}

		this.modulePath = modulePath;
	}

	public String getModulePath()
	{
		return this.modulePath;
	}

	public CommandLine.ParameterException moduleNotFoundException()
	{
		return new CommandLine.ParameterException(this.spec.commandLine(), MODULE_NOT_DEFINED);
	}

	public CommandLine.ParameterException pathAlreadyExistsException(Path path)
	{
		var message = PATH_ALREADY_EXISTS.formatted(path);
		return new CommandLine.ParameterException(this.spec.commandLine(), message);
	}
}
