package com.freifeld.tools.quephaestus.configuration;

import java.util.Map;

public class QuephaestusConfiguration
{
	private String projectName;
	private String namespace;
	private String templatesFolder;
	private Map<String, QuephaestusCommand> commands;

	public String getProjectName()
	{
		return this.projectName;
	}

	public void setProjectName(String projectName)
	{
		this.projectName = projectName;
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

	public Map<String, QuephaestusCommand> getCommands()
	{
		return this.commands;
	}

	public void setCommands(Map<String, QuephaestusCommand> commands)
	{
		this.commands = commands;
	}
}
