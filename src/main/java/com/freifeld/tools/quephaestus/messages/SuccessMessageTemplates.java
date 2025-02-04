package com.freifeld.tools.quephaestus.messages;

import picocli.CommandLine.Model.CommandSpec;

import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

import static picocli.CommandLine.Help.Ansi.AUTO;

public class SuccessMessageTemplates
{
	public static final String SUCCESSFUL_FORGING = AUTO.string("""
	                                                            \uD83C\uDF89 @|bold,fg(green) DONE|@ \uD83C\uDF89
	                                                            Created:
	                                                            %s
	                                                            """.strip());

	public static final String POSSIBLE_ELEMENTS = "Possible element names are: %s";

	public static void forgeSuccessMessage(CommandSpec spec, Collection<Path> paths)
	{
		final var createdPathsFormatted = paths.stream().map(p -> " - " + p).collect(Collectors.joining("\n"));
		final var message = SUCCESSFUL_FORGING.formatted(createdPathsFormatted);
		spec.commandLine().getOut().println(message);
	}

	public static void possibleElements(CommandSpec spec, Collection<String> elements)
	{
		final var message = POSSIBLE_ELEMENTS.formatted(elements);
		spec.commandLine().getOut().println(message);
	}
}
