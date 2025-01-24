package com.freifeld.tools.quephaestus;

import io.quarkus.qute.Engine;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class BlacksmithForge
{
	public static final Set<String> COMMON_TEMPLATE_PARAMETERS = Set.of("namespace", "projectName");

	@Inject
	private Engine engine;

	public Template parse(Path templatePath)
	{
		try
		{
			String content = Files.readString(templatePath);
			var template = this.engine.parse(content);
			return template;
		}
		catch (IOException e)
		{
			throw new RuntimeException(e); // TODO
		}
	}

	public String forge(TemplateInstance template, Map<String, String> data)
	{
		var templateInstance = template;
		for (var entry : data.entrySet())
		{
			templateInstance = templateInstance.data(entry.getKey(), entry.getValue());
		}

		return templateInstance.render();
	}

	private Set<String> getExpressionParts(Template template)
	{
		return template.getExpressions()
		               .stream()
		               .map(ex -> ex.getParts().getFirst().getName())
		               .collect(Collectors.toSet());
	}

	// TODO maybe prepare a "blueprint" and then pass to forge
	public String forgeOne(Path templateFile, DatamapSource datasource)
	{
		final var template = this.parse(templateFile);
		final var templateData = datasource.fillDataMap(this.getExpressionParts(template));
		return this.forge(template.instance(), templateData);
	}

	public void forgeBlueprint()
	{
		throw new UnsupportedOperationException("Not supporting blueprint at the moment");
	}
}
