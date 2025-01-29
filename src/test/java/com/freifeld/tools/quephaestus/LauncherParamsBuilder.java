package com.freifeld.tools.quephaestus;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

public class LauncherParamsBuilder
{
	public String subCommand;
	public File configFilePath;
	public String workingDirectory;
	public String baseDirectory;
	public String moduleName;
	public String element;

	public LauncherParamsBuilder withConfigPath()
	{
		try
		{
			URI resource = this.getClass().getClassLoader().getResource("project-structure.yaml").toURI();
			this.configFilePath = new File(resource);
			return this;
		}
		catch (URISyntaxException e)
		{
			throw new RuntimeException(e); // TODO
		}
	}

	public LauncherParamsBuilder withConfigPath(File path)
	{
		this.configFilePath = path;
		return this;
	}

	public LauncherParamsBuilder withWorkingDirectory(String workingDirectory) {
		this.workingDirectory = workingDirectory;
		return this;
	}

	public LauncherParamsBuilder withBaseDirectory(String baseDirectory) {
		this.baseDirectory = baseDirectory;
		return this;
	}

	public LauncherParamsBuilder withModule(String module)
	{
		this.moduleName = module;
		return this;
	}

	public LauncherParamsBuilder withSubCommandName(String subCommand)
	{
		this.subCommand = subCommand;
		return this;
	}

	public LauncherParamsBuilder withElement(String element)
	{
		this.element = element;
		return this;
	}

	public String[] build()
	{
		var commands = new LinkedList<String>();

		if (this.subCommand != null)
		{
			commands.add(this.subCommand);
		}
		if (this.configFilePath != null)
		{
			commands.add("--file-path=" + this.configFilePath);
		}
		if (this.moduleName != null)
		{
			commands.add("--module=" + this.moduleName);
		}


		if (this.element != null)
		{
			commands.add(this.element);
		}

		var asArray = new String[commands.size()];
		return commands.toArray(asArray);
	}
}