package com.freifeld.tools.quephaestus.configuration;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

//TODO Preferably this would be injected but something didn't work with Picocli and injections.. need to check it out
public class ConfigReader
{
	public static QuephaestusConfiguration readConfig(String configFile)
	{
		try (var readFile = new FileInputStream(configFile))
		{
			var options = new LoaderOptions();
			var yaml = new Yaml(options);
			return yaml.loadAs(readFile, QuephaestusConfiguration.class);
		}
		catch (FileNotFoundException e)
		{
			throw new RuntimeException(e); // TODO
		}
		catch (IOException e)
		{
			throw new RuntimeException(e); // TODO
		}
	}
}
