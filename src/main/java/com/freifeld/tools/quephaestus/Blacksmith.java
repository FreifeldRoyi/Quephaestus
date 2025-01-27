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

	private Set<String> getExpressionParts(Template template)
	{
		return template.getExpressions()
		               .stream()
		               .map(ex -> ex.getParts().getFirst().getName())
		               .collect(Collectors.toSet());
	}

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
		final var datamap = new HashMap<String, String>();
		// 1. Configuration
		datamap.put("project", blueprint.configuration().getProject());
		datamap.put("namespace", blueprint.configuration().getNamespace());
		datamap.put("module", blueprint.moduleName());

		return datamap;
	}

	private Map<String, String> collectInterpolationSlots(
			Map<String, String> datasource,
			Template template,
			Template filenameTemplate,
			Template commandPath
	)
	{
		// 2. Template
		final var templateInterpolationSlots = this.getExpressionParts(template);

		// 3. filenameTemplate
		final var filenameInterpolationSlots = this.getExpressionParts(filenameTemplate);

		// 4. commandPath
		final var commandPathInterpolationSlots = this.getExpressionParts(commandPath);

		// Collect all
		final var interpolationSlots = Stream.of(
				                                     templateInterpolationSlots,
				                                     filenameInterpolationSlots,
				                                     commandPathInterpolationSlots)
		                                     .flatMap(Collection::stream)
		                                     .collect(Collectors.toSet());

		/*
		 TODO when I'll provide a solution for data file (instead of manual input) need to change the following
		  also, I don't like this code. It depends on System.in
		 */
		var inputDatasource = new InputStreamDatamapSource(
				datasource,
		                                                   System.in,
		                                                   interpolationSlot -> System.out.printf(
				                                                   "%s?%n",
				                                                   interpolationSlot));
		return inputDatasource.fillDataMap(interpolationSlots);
		// TODO end here
	}

	public void forgeOnce(Blueprint blueprint)
	{
		final var configuration = blueprint.configuration();
		final var commandParameterConfiguration = configuration.getCommands().get(blueprint.commandParameter());
		final var template = this.forge.parse(blueprint.templatePath());
		final var filenameTemplate = this.forge.parse(commandParameterConfiguration.getNamePattern());
		final var commandPathTemplate = this.forge.parse(commandParameterConfiguration.getPath());

		final var datasource = this.collectInterpolationSlots(
				this.initialInterpolationSlots(blueprint),
				template,
				filenameTemplate,
				commandPathTemplate);

		var fileToWrite = this.forge.render(template, datasource);
		var filenameRendered = this.forge.render(filenameTemplate, datasource);
		var commandPathRendered = this.forge.render(commandPathTemplate, datasource);

		var path = this.prepareOutputPath(
				blueprint.rootPath(),
				blueprint.modulePath(),
				commandPathRendered,
				filenameRendered);

		try
		{
			this.writer.writeContent(path, fileToWrite);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e); // TODO
		}
	}
}
