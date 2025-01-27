package com.freifeld.tools.quephaestus.exceptions;

import jakarta.enterprise.context.ApplicationScoped;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.ParameterException;

// TODO cannot inject in mixins.. sigh..
@ApplicationScoped
public class ExceptionGenerator
{
	public ParameterException makeException(CommandSpec spec, String templateMessage, Object... args)
	{
		var message = args == null || args.length == 0 ? templateMessage : templateMessage.formatted(args);
		return new ParameterException(spec.commandLine(), message);
	}
}
