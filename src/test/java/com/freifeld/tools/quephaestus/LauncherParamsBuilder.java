package com.freifeld.tools.quephaestus;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

public class LauncherParamsBuilder
{
	public String subCommand;
	public File configFilePath;
	public String moduleName;
	public String element;

	public LauncherParamsBuilder withConfigPath()
	{
		URI resource = null;
		try
		{
			resource = this.getClass().getClassLoader().getResource("project-structure.yaml").toURI();
		}
		catch (URISyntaxException e)
		{
			throw new RuntimeException(e); // TODO
		}
		this.configFilePath = new File(resource);
//		this.configFilePath = resource.toExternalForm().replaceFirst(resource.getProtocol() + ":", "");
		return this;
	}

	public LauncherParamsBuilder withConfigPath(File path)
	{
		this.configFilePath = path;
		//		this.commands.add("--file-path=" + path);
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

	public LauncherParamsBuilder withElement(String element) {
		this.element = element;
		return this;
	}

	public String[] build()
	{
		var commands = new LinkedList<String>();
		//		commands.add("quaphestus");

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


		if (this.element != null) {
			commands.add(this.element);
		}

		var asArray = new String[commands.size()];
		return commands.toArray(asArray);
	}
}