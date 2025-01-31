package com.freifeld.tools.quephaestus.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

//TODO Preferably this would be injected but something didn't work with Picocli and injections.. need to check it out
public class ConfigReader
{
	public static QuephaestusConfiguration readConfig(String configFile)
	{
		final var objectMapper = new ObjectMapper(new YAMLFactory()).registerModule(new Jdk8Module());
		try (var readFile = new FileInputStream(configFile))
		{
			return objectMapper.readValue(readFile, QuephaestusConfiguration.class);
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
