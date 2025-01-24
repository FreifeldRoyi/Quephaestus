package com.freifeld.tools.quephaestus.exceptions;

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
}

