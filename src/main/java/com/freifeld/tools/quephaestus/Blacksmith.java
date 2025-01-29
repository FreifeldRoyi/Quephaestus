package com.freifeld.tools.quephaestus;

import com.freifeld.tools.quephaestus.configuration.Blueprint;
import com.freifeld.tools.quephaestus.exceptions.MissingDataException;
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
import java.util.function.Predicate;
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
		final var externalMappings = blueprint.mappings().entrySet().stream().reduce(
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
			Template elementPath
	) throws MissingDataException
	{
		// 3. Template
		final var templateInterpolationSlots = this.forge.getInterpolationSlotsFrom(template);

		// 4. filenameTemplate
		final var filenameInterpolationSlots = this.forge.getInterpolationSlotsFrom(filenameTemplate);

		// 5. elementPath
		final var elementPathInterpolationSlots = this.forge.getInterpolationSlotsFrom(elementPath);

		// Collect all
		final var interpolationSlots = Stream.of(
				                                     templateInterpolationSlots,
				                                     filenameInterpolationSlots,
				                                     elementPathInterpolationSlots)
		                                     .flatMap(Collection::stream)
		                                     .filter(slot -> !datasource.containsKey(slot))
		                                     .collect(Collectors.toSet());


		if (!datasource.keySet().containsAll(interpolationSlots))
		{
			final var missing = interpolationSlots.stream()
			                                      .filter(Predicate.not(datasource::containsKey))
			                                      .collect(Collectors.toSet());
			throw new MissingDataException(missing);
		}
		/*
		 TODO when I'll provide a solution for data file (instead of manual input) need to change the following
		  also, I don't like this code. It depends on System.in
		 */
		//		try (var inputDatasource = new InputStreamDatamapSource(System.in, slot -> System.out.printf("%s?%n", slot)))
		//		{
		//			for (var slot : interpolationSlots)
		//			{
		//				var value = inputDatasource.valueFor(slot);
		//				datasource.put(slot, value);
		//			}
		//		}
	}

	public Set<Path> forge(Blueprint blueprint) throws MissingDataException
	{
		final var configuration = blueprint.configuration();
		final var datasource = this.initialInterpolationSlots(blueprint);
		final var filesToWrite = new HashMap<Path, String>();
		for (var entry : blueprint.templatePaths().entrySet())
		{
			final var element = entry.getKey();
			final var templatePath = entry.getValue();
			final var elementConfiguration = configuration.getElements().get(element);

			final var template = this.forge.parse(templatePath);
			final var filenameTemplate = this.forge.parse(elementConfiguration.getNamePattern());
			final var elementPathTemplate = this.forge.parse(elementConfiguration.getPath());

			this.fillDatasourceFromTemplate(datasource, template, filenameTemplate, elementPathTemplate);
			var fileToWrite = this.forge.render(template, datasource);
			var filenameRendered = this.forge.render(filenameTemplate, datasource);
			var elementPathRendered = this.forge.render(elementPathTemplate, datasource);

			var path = this.prepareOutputPath(
					blueprint.rootPath(),
					blueprint.modulePath(),
					elementPathRendered,
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
