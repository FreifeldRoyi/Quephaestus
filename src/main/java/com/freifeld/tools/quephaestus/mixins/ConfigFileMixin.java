package com.freifeld.tools.quephaestus.mixins;

import com.freifeld.tools.quephaestus.configuration.ConfigReader;
import com.freifeld.tools.quephaestus.configuration.QuephaestusConfiguration;
import picocli.CommandLine.Option;

import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigFileMixin
{
	private QuephaestusConfiguration configuration;

	private Path configPath;
	private Path templatesPath;

	@Option(names = { "-f", "--file-path" },
	        description = "Configuration file path",
	        order = 0,
	        required = false)
	private void setConfigFileOption(String fileName)
	{
		final var config = ConfigReader.readConfig(fileName);

		// TODO validate configuration or maybe with Jackson once YAML is built in as well

		this.configuration = config; // TODO default file path not handled instead of this.configFile
		this.setConfigPath(fileName);
		this.setTemplatesFolder();
	}

	private void setConfigPath(String fileName) {
		this.configPath = Path.of(fileName);
	}
	private void setTemplatesFolder() {
		final var templatesConfig = Path.of(this.configuration.getTemplatesFolder());
		final var configPath = this.configPath.getParent();
		final var path = templatesConfig.isAbsolute() ?
		                          templatesConfig :
		                          configPath.resolve(templatesConfig);

		if (!Files.isDirectory(path)) {
			throw new RuntimeException("Templates folder does not exist " + path.toString()); // TODO change to proper exception
		}

		this.templatesPath = path;
	}

	public QuephaestusConfiguration configuration()
	{
		return this.configuration;
	}

	public Path configPath()
	{
		return this.configPath;
	}

	public Path templatePath() {
		return this.templatesPath;
	}
}
