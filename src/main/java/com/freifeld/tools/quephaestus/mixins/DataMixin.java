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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

import static com.freifeld.tools.quephaestus.messages.ExceptionMessageTemplates.pathDoesNotExist;

@ApplicationScoped
public class DataMixin
{
	private DataMixin.Exclusive exclusive;
	private CommandSpec spec;
	private Map<String, String> mappings;

	@ArgGroup(exclusive = true)
	public void setExclusive(DataMixin.Exclusive exclusive)
	{
		this.exclusive = exclusive;
		this.exclusive.mixin = this;
	}

	@Spec(Target.MIXEE)
	public void setSpec(CommandSpec spec)
	{
		this.spec = spec;
	}

	public Map<String, String> mappings()
	{
		return this.mappings != null ? this.mappings : Collections.emptyMap();
	}

	public static class Exclusive
	{
		DataMixin mixin;

		private void parseContent(String content)
		{
			final var mapper = new ObjectMapper();
			try (final var parser = mapper.createParser(content))
			{
				this.mixin.mappings = parser.readValueAs(new TypeReference<Map<String, String>>() { });
			}
			catch (IOException e)
			{
				throw new RuntimeException(e); // TODO
			}
		}

		@Option(names = { "-d", "--data" }, description = "Inline json data containing mappings")
		public void setData(String manualData)
		{
			this.parseContent(manualData);
		}

		@Option(names = { "-p", "--payload" }, description = "Data file containing mappings")
		public void setPayload(String filePath)
		{
			final var path = Path.of(filePath);
			if (!Files.exists(path))
			{
				throw pathDoesNotExist(this.mixin.spec, path);
			}

			try
			{
				final var fileContent = Files.readString(path);
				this.parseContent(fileContent);
			}
			catch (IOException e)
			{
				throw new RuntimeException(e); // TODO
			}
		}
	}
}
