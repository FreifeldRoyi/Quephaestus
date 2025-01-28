package com.freifeld.tools.quephaestus;

import io.quarkus.qute.Engine;
import io.quarkus.qute.Template;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
		for (var slot : this.getInterpolationSlotsFrom(template))
		{
			templateInstance = templateInstance.data(slot, data.get(slot));
		}

		return templateInstance.render();
	}

	// TODO will be the only place that can extract expressions from template.
	//  by doing that, I'm containing qute function handling to a single place
	public Set<String> getInterpolationSlotsFrom(Template template) {
		return template.getExpressions().stream().map(expression -> expression.getParts().getFirst().getName()).collect(
				Collectors.toSet());
	}
}
