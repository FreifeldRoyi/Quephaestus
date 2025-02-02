package com.freifeld.tools.quephaestus.configuration;

import java.nio.file.Path;
import java.util.Map;

public record Blueprint(
		Map<String, Path> templatePaths,
		Map<String, String> mappings,
		String moduleName,
		Path modulePath,
		QuephaestusConfiguration configuration,
		Path rootPath,
		boolean interactive
)
{
	public Blueprint(
			Path templatePath,
			String element,
			Map<String, String> mappings,
			String moduleName,
			Path modulePath,
			QuephaestusConfiguration configuration,
			Path rootPath,
			boolean interactive
	)
	{
		this(Map.of(element, templatePath), mappings, moduleName, modulePath, configuration, rootPath, interactive);
	}
}
