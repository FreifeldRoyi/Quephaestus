package com.freifeld.tools.quephaestus.configuration;

import java.nio.file.Path;

public record Blueprint(
		Path templatePath,
		String commandParameter,
		String moduleName,
		Path modulePath,
		QuephaestusConfiguration configuration,
		Path rootPath
)
{ }
