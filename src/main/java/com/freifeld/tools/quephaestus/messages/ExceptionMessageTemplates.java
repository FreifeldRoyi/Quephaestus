package com.freifeld.tools.quephaestus.messages;

import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.ParameterException;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class ExceptionMessageTemplates
{
	public static final String PATH_ALREADY_EXISTS = """
	                                                 Path %s already exists, is not a directory or not empty.
	                                                 Please provide another non-existent path or empty the given directory
	                                                 """.trim();
	public static final String PATH_DOES_NOT_EXIST = "Path %s does not exist";
	public static final String MODULE_NOT_DEFINED = "Module is not defined";
	public static final String INVALID_ELEMENT = """
	                                             '%s' is not a valid element name. Possible values are: %s.
	                                             Loaded configuration from: %s
	                                             """.trim();
	public static final String DIRECTORY_DOES_NOT_EXIST = "Provided directory does not exist: %s";
	public static final String PATH_CANNOT_BE_ABSOLUTE_PATH = "Please provide relative path instead of an absolute path: %s";
	public static final String TEMPLATES_WERE_NOT_FOUND = """
	                                                      Could not find requested templates.
	                                                      Given   (%d): %s
	                                                      Missing (%d):
	                                                      %s
	                                                      """.trim();
	public static final String TEMPLATE_WAS_NOT_FOUND = "Could not find requested template. Given: %s";
	public static final String INTERPOLATION_SLOTS_MISSING_VALUES = """
	                                                                Some expressions are missing a value:
	                                                                %s
	                                                                """.trim();

	public static ParameterException directoryDoesNotExistException(CommandSpec spec, Path path)
	{
		var message = DIRECTORY_DOES_NOT_EXIST.formatted(path);
		return new ParameterException(spec.commandLine(), message);
	}

	public static ParameterException pathDoesNotExist(CommandSpec spec, Path path)
	{
		var message = PATH_DOES_NOT_EXIST.formatted(path);
		return new ParameterException(spec.commandLine(), message);
	}

	public static ParameterException absolutePathException(CommandSpec spec, Path path)
	{
		return absolutePathException(spec, path.toString());
	}

	public static ParameterException absolutePathException(CommandSpec spec, String path)
	{
		var message = PATH_CANNOT_BE_ABSOLUTE_PATH.formatted(path);
		return new ParameterException(spec.commandLine(), message);
	}

	public static ParameterException moduleNotFoundException(CommandSpec spec)
	{
		return new ParameterException(spec.commandLine(), MODULE_NOT_DEFINED);
	}

	public static ParameterException pathAlreadyExistsException(CommandSpec spec, Path path)
	{
		var message = PATH_DOES_NOT_EXIST.formatted(path);
		return new ParameterException(spec.commandLine(), message);
	}

	public static ParameterException invalidElementException(
			CommandSpec spec,
			String element,
			Path configPath,
			Set<String> possibleKeys
	)
	{
		final var message = INVALID_ELEMENT.formatted(element, possibleKeys, configPath);
		return new ParameterException(spec.commandLine(), message);
	}

	public static ParameterException templatesWereNotFound(CommandSpec spec, Set<String> expected, Set<Path> missing)
	{
		final var message = TEMPLATES_WERE_NOT_FOUND.formatted(
				expected.size(),
				expected,
				missing.size(),
				formatList(missing));
		return new ParameterException(spec.commandLine(), message);
	}

	public static ParameterException templateWasNotFound(CommandSpec spec, String expected)
	{
		final var message = TEMPLATE_WAS_NOT_FOUND.formatted(expected);
		return new ParameterException(spec.commandLine(), message);
	}

	public static ParameterException interpolationSlotsMissingValues(CommandSpec spec, Set<String> missing)
	{
		final var message = INTERPOLATION_SLOTS_MISSING_VALUES.formatted(formatList(missing));
		return new ParameterException(spec.commandLine(), message);
	}

	private static String formatList(Collection<?> coll)
	{
		return coll.stream().map(p -> " - " + p).collect(Collectors.joining("\n"));
	}
}

