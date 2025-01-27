package com.freifeld.tools.quephaestus.exceptions;

import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.ParameterException;

import java.nio.file.Path;

public class ExceptionMessageTemplates
{
	public static final String PATH_ALREADY_EXISTS = """
	                                                 Path %s already exists, is not a directory or not empty.
	                                                 Please provide another non-existent path or empty the given directory
	                                                 """.trim();


	public static final String MODULE_NOT_DEFINED = "Module is not defined";

	public static final String INVALID_PARAMETER = """
	                                               '%s' is not a valid command parameter name. Possible values are: %s.
	                                               Loaded configuration from: %s
	                                               """.trim();

	public static final String DIRECTORY_DOES_NOT_EXIST = "Provided directory does not exist: %s";

	public static final String PATH_CANNOT_BE_ABSOLUTE_PATH = "Please provide relative path instead of an absolute path: %s";

	public static ParameterException directoryDoesNotExistException(CommandSpec spec, Path path)
	{
		var message = DIRECTORY_DOES_NOT_EXIST.formatted(path);
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
		var message = PATH_ALREADY_EXISTS.formatted(path);
		return new ParameterException(spec.commandLine(), message);
	}
}

