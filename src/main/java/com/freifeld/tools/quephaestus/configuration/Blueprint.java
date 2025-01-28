package com.freifeld.tools.quephaestus.configuration;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

public record Blueprint(
		Map<String, Path> templatePaths,
		Map<String, String> extraMappings,
		String moduleName,
		Path modulePath,
		QuephaestusConfiguration configuration,
		Path rootPath
)
{
	public Blueprint(
			Path templatePath,
			String commandParameter,
			String moduleName,
			Path modulePath,
			QuephaestusConfiguration configuration,
			Path rootPath
	)
	{
		this(
				Map.of(commandParameter, templatePath),
				Collections.emptyMap(),
				moduleName,
				modulePath,
				configuration,
				rootPath);
	}
}
