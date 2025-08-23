package com.freifeld.tools.quephaestus.core;

import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Consumer;

public class InputStreamDatamapSource implements InterpolationSlotResolver
{
	private final InputStream inputStream;
	private final Consumer<String> descriptionPrinter;
	private final Scanner scanner;

	public InputStreamDatamapSource(InputStream inputStream, Consumer<String> descriptionPrinter)
	{
		this.inputStream = inputStream;
		this.descriptionPrinter = descriptionPrinter;
		this.scanner = new Scanner(this.inputStream);
	}

	@Override
	public String valueFor(String slot)
	{
		if (this.descriptionPrinter != null) {
			this.descriptionPrinter.accept(slot);
		}
		return this.scanner.nextLine();
	}

	@Override
	public void close()
	{
		this.scanner.close();
	}
}
