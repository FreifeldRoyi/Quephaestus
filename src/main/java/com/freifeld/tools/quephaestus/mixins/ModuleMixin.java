package com.freifeld.tools.quephaestus.mixins;

import jakarta.enterprise.context.ApplicationScoped;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Spec.Target;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static com.freifeld.tools.quephaestus.messages.ExceptionMessageTemplates.absolutePathException;
import static com.freifeld.tools.quephaestus.messages.ExceptionMessageTemplates.moduleNotFoundException;

@ApplicationScoped
public class ModuleMixin
{
	private String moduleName;
	private Path modulePath;

	private CommandSpec spec;

	@Spec(Target.MIXEE)
	public void setCommandSpec(CommandSpec spec)
	{
		this.spec = spec;
	}

	@Option(names = { "-m", "--module" },
	        description = "The name of the module to generate in",
	        order = 3,
	        required = true)
	public void setModule(String module) throws IOException
	{
		if (module == null || module.isBlank())
		{
			throw moduleNotFoundException(this.spec);
		}

		// transform a.b.c -> a/b/c
		var moduleFormatted = String.join(File.separator, module.split("\\."));
		var modulePath = Path.of(moduleFormatted);
		if (modulePath.isAbsolute())
		{
			throw absolutePathException(this.spec, module);
		}

		this.moduleName = modulePath.getFileName().toString();
		this.modulePath = modulePath;
	}

	public Path modulePath()
	{
		return this.modulePath;
	}

	public String moduleName()
	{
		return this.moduleName;
	}
}
