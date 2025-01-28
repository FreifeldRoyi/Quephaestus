package com.freifeld.tools.quephaestus.configuration;

import java.util.Map;
import java.util.Set;

public class BlueprintDefinition
{
	private Set<String> parameters;
	private Map<String, String> mappings;

	public Set<String> getParameters()
	{
		return this.parameters;
	}

	public void setParameters(Set<String> parameters)
	{
		this.parameters = parameters;
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
