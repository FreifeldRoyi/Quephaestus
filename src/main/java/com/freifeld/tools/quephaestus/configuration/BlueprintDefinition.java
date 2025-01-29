package com.freifeld.tools.quephaestus.configuration;

import java.util.Map;
import java.util.Set;

public class BlueprintDefinition
{
	private Set<String> elements;
	private Map<String, String> mappings;

	public Set<String> getElements()
	{
		return this.elements;
	}

	public void setElements(Set<String> elements)
	{
		this.elements = elements;
	}

	public Map<String, String> getMappings()
	{
		return mappings;
	}

	public void setMappings(Map<String, String> mappings)
	{
		this.mappings = mappings;
	}
}
