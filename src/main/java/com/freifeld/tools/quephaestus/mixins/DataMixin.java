package com.freifeld.tools.quephaestus.mixins;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Spec.Target;

import java.io.IOException;
import java.util.Map;

@ApplicationScoped
public class DataMixin
{
	private DataMixin.Exclusive exclusive;

	private CommandSpec spec;

	@ArgGroup()
	public void setExclusive(DataMixin.Exclusive exclusive)
	{
		this.exclusive = exclusive;
	}

	@Spec(Target.MIXEE)
	public void setSpec(CommandSpec spec)
	{
		this.spec = spec;
	}

	public static class Exclusive
	{
		@Option(names = { "-d", "--data" },
		        description = "Inline json data containing mappings")
		public void setData(String json)
		{
			final var objectMapper = new ObjectMapper();
			try (final var parser = objectMapper.createParser(json))
			{
				var value = parser.<Map<String, String>> readValueAs(new TypeReference<Map<String, String>>() { });

				System.out.println(value);
			}
			catch (IOException e)
			{
				throw new RuntimeException(e); // TODO
			}
		}

		@Option(names = { "-p", "--payload" },
		        description = "Data file containing mappings")
		public void setPayload(String path)
		{

		}
	}
}
