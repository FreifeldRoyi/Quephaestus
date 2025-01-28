package com.freifeld.tools.quephaestus;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;

public class InputStreamDatamapSource implements DatamapSource
{
	private final InputStream inputStream;
	private final Optional<Consumer<String>> descriptionPrinter;

	public InputStreamDatamapSource(InputStream inputStream, Consumer<String> descriptionPrinter)
	{
		this.inputStream = inputStream;
		this.descriptionPrinter = Optional.ofNullable(descriptionPrinter);
	}

	@Override
	public void fillDataMap(Map<String, String> datasource, Set<String> slots)
	{
		try (final var scanner = new Scanner(this.inputStream))
		{
			for (var slot : slots)
			{
				this.descriptionPrinter.ifPresent(printer -> printer.accept(slot));
				var value = scanner.next();
				datasource.put(slot, value);
			}
		}
	}
}
