package com.freifeld.tools.quephaestus.configuration;

import java.util.Map;

public record QuephaestusConfiguration(
		String project,
		String namespace,
		String templatesFolder,
		/**
		 * Relative directory location under working directory to take into consideration
		 * e.g.
		 * In Java, Generally speaking, code resides under src/main/java/...
		 * In this case, src/main/java might be a good candidate for a base directory
		 */
		String baseDirectory,
		Map<String, Element> elements,
		Map<String, BlueprintDefinition> blueprints
)
{
}
