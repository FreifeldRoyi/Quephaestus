package com.freifeld.tools.quephaestus;

import com.freifeld.tools.quephaestus.configuration.Blueprint;
import io.quarkus.qute.Template;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class Blacksmith
{
	@Inject
	Forge forge;

	@Inject
	FileSystemWriter writer;

	/**
	 * Creates the final path to write: root/module/package/filename
	 *
	 * @return a fully constructed path including all parts:
	 * e.g.</br>
	 * root -> <code>/tmp</code></br>
	 * module -> <code>my/module/path/modulename</code></br>
	 * package -> <code>controllers/api/v1</code></br>
	 * filename -> <code>MyController.java</code></br>
	 * result ---> <code>/tmp/my/module/path/modulename/controllers/api/v1/MyController.java</code>
	 */
	private Path prepareOutputPath(Path root, Path modulePath, String packagePath, String filename)
	{
		try
		{
			/*
			 outputPath should always be absolute since root is constructed from working directory as the first stage, and it is transformed to absolute form
			 */
			final var outputDirectory = root.resolve(modulePath).resolve(packagePath);
			Files.createDirectories(outputDirectory);
			return outputDirectory.resolve(filename);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e); // TODO
		}
	}

	private Map<String, String> initialInterpolationSlots(Blueprint blueprint)
	{
		final var datasource = new HashMap<String, String>();
		// 1. Configuration
		datasource.put("project", blueprint.configuration().getProject());
		datasource.put("namespace", blueprint.configuration().getNamespace());
		datasource.put("module", blueprint.moduleName());

		// 2. External mappings - can be static or set from well known interpolation slots
		final var externalMappings = blueprint.extraMappings().entrySet().stream().reduce(
				new HashMap<String, String>(), (acc, entry) -> {
					final var mappingTemplate = this.forge.parse(entry.getValue());
					final var slots = this.forge.getInterpolationSlotsFrom(mappingTemplate);
					final var value = slots.isEmpty() ?
					                  entry.getValue() :
					                  this.forge.render(mappingTemplate, datasource);
					acc.put(entry.getKey(), value);
					return acc;

				}, (map1, map2) -> {
					map1.putAll(map2);
					return map1;
				});
		datasource.putAll(externalMappings);

		return datasource;
	}

	private void fillDatasourceFromTemplate(
			Map<String, String> datasource,
			Template template,
			Template filenameTemplate,
			Template commandPath
	)
	{
		// 3. Template
		final var templateInterpolationSlots = this.forge.getInterpolationSlotsFrom(template);

		// 4. filenameTemplate
		final var filenameInterpolationSlots = this.forge.getInterpolationSlotsFrom(filenameTemplate);

		// 5. commandPath
		final var commandPathInterpolationSlots = this.forge.getInterpolationSlotsFrom(commandPath);

		// Collect all
		final var interpolationSlots = Stream.of(
				                                     templateInterpolationSlots,
				                                     filenameInterpolationSlots,
				                                     commandPathInterpolationSlots)
		                                     .flatMap(Collection::stream)
		                                     .filter(slot -> !datasource.containsKey(slot))
		                                     .collect(Collectors.toSet());

		/*
		 TODO when I'll provide a solution for data file (instead of manual input) need to change the following
		  also, I don't like this code. It depends on System.in
		 */
		try (var inputDatasource = new InputStreamDatamapSource(System.in, slot -> System.out.printf("%s?%n", slot)))
		{
			for (var slot : interpolationSlots)
			{
				var value = inputDatasource.valueFor(slot);
				datasource.put(slot, value);
			}
		}
	}

	public Set<Path> forge(Blueprint blueprint)
	{
		final var configuration = blueprint.configuration();
		final var datasource = this.initialInterpolationSlots(blueprint);
		final var filesToWrite = new HashMap<Path, String>();
		for (var entry : blueprint.templatePaths().entrySet())
		{
			final var commandParameter = entry.getKey();
			final var templatePath = entry.getValue();
			final var commandParameterConfiguration = configuration.getCommands().get(commandParameter);

			final var template = this.forge.parse(templatePath);
			final var filenameTemplate = this.forge.parse(commandParameterConfiguration.getNamePattern());
			final var commandPathTemplate = this.forge.parse(commandParameterConfiguration.getPath());

			this.fillDatasourceFromTemplate(datasource, template, filenameTemplate, commandPathTemplate);
			var fileToWrite = this.forge.render(template, datasource);
			var filenameRendered = this.forge.render(filenameTemplate, datasource);
			var commandPathRendered = this.forge.render(commandPathTemplate, datasource);

			var path = this.prepareOutputPath(
					blueprint.rootPath(),
					blueprint.modulePath(),
					commandPathRendered,
					filenameRendered);
			filesToWrite.put(path, fileToWrite);
		}

		/*
		The processes of resolving the values for interpolation slots and writing the files are separated on purpose
			even though they can be in-lined. I wanted to create an experience of first getting all slots, and only
			then doing everything else
		 */
		for (var fileEntry : filesToWrite.entrySet())
		{
			try
			{
				this.writer.writeContent(fileEntry.getKey(), fileEntry.getValue());
			}
			catch (IOException e)
			{
				throw new RuntimeException(e); // TODO
			}
		}

		return filesToWrite.keySet();
	}
}
