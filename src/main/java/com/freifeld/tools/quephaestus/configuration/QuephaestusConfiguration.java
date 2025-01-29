package com.freifeld.tools.quephaestus.configuration;

import java.util.Map;

public class QuephaestusConfiguration
{
	private String project;
	private String namespace;
	private String templatesFolder;

	/**
	 * Relative directory location under working directory to take into consideration
	 * e.g.
	 * In Java, Generally speaking, code resides under src/main/java/...
	 * In this case, src/main/java might be a good candidate for a base directory
	 */
	private String baseDirectory;
	private Map<String, Element> elements;
	private Map<String, BlueprintDefinition> blueprints;

	public String getProject()
	{
		return this.project;
	}

	public void setProject(String project)
	{
		this.project = project;
	}

	public String getNamespace()
	{
		return this.namespace;
	}

	public void setNamespace(String namespace)
	{
		this.namespace = namespace;
	}

	public String getTemplatesFolder()
	{
		return this.templatesFolder;
	}

	public void setTemplatesFolder(String templatesFolder)
	{
		this.templatesFolder = templatesFolder;
	}

	public String getBaseDirectory()
	{
		return baseDirectory;
	}

	public void setBaseDirectory(String baseDirectory)
	{
		this.baseDirectory = baseDirectory;
	}

	public Map<String, Element> getElements()
	{
		return this.elements;
	}

	public void setElements(Map<String, Element> elements)
	{
		this.elements = elements;
	}

	public Map<String, BlueprintDefinition> getBlueprints()
	{
		return blueprints;
	}

	public void setBlueprints(Map<String, BlueprintDefinition> blueprints)
	{
		this.blueprints = blueprints;
	}
}
