package com.freifeld.tools.quephaestus;

import io.quarkus.qute.Engine;
import io.quarkus.qute.Template;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@ApplicationScoped
public class Forge
{
	@Inject
	private Engine engine;

	public Template parse(Path templatePath)
	{
		try
		{
			String content = Files.readString(templatePath);
			return this.parse(content);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e); // TODO
		}
	}

	public Template parse(String content)
	{
		return this.engine.parse(content);
	}

	public String render(Template template, Map<String, String> data)
	{
		var templateInstance = template.instance();
		for (var expression : template.getExpressions())
		{
			String name = expression.getParts().getFirst().getName();
			templateInstance = templateInstance.data(name, data.get(name));
		}

		return templateInstance.render();
	}
}
