package com.freifeld.tools.quephaestus.exceptions;

import jakarta.enterprise.context.ApplicationScoped;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;

// TODO cannot inject in mixins.. sigh..
@ApplicationScoped
public class ExceptionGenerator
{
	public CommandLine.ParameterException makeException(CommandSpec spec, String templateMessage, Object... args)
	{
		var message = args == null || args.length == 0 ? templateMessage : templateMessage.formatted(args);
		return new CommandLine.ParameterException(spec.commandLine(), message);
	}
}
