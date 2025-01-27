package com.freifeld.tools.quephaestus;

import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;

public class InputStreamDatamapSource implements DatamapSource
{
	private final InputStream inputStream;
	private final Optional<Consumer<String>> descriptionPrinter;
	private final Map<String, String> datamap;

	public InputStreamDatamapSource(
			Map<String, String> initialDatamap,
			InputStream inputStream,
			Consumer<String> descriptionPrinter
	)
	{
		this.inputStream = inputStream;
		this.descriptionPrinter = Optional.ofNullable(descriptionPrinter);
		this.datamap = new HashMap<>(initialDatamap);
	}

	@Override
	public Map<String, String> fillDataMap(Set<String> requiredParts)
	{
		try (final var scanner = new Scanner(this.inputStream))
		{
			for (var name : requiredParts)
			{
				if (!this.datamap.containsKey(name))
				{
					this.descriptionPrinter.ifPresent(printer -> printer.accept(name));
					var value = scanner.next();
					this.datamap.put(name, value);
				}
			}
		}
		return Map.copyOf(this.datamap);
	}
}
