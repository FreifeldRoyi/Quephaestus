package com.freifeld.tools.quephaestus;

import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Consumer;

public class InputStreamDatamapSource implements DatamapSource
{
	private final InputStream inputStream;
	private final Optional<Consumer<String>> descriptionPrinter;
	private final Scanner scanner;

	public InputStreamDatamapSource(InputStream inputStream, Consumer<String> descriptionPrinter)
	{
		this.inputStream = inputStream;
		this.descriptionPrinter = Optional.ofNullable(descriptionPrinter);
		this.scanner = new Scanner(this.inputStream);
	}

	@Override
	public String valueFor(String slot)
	{
		this.descriptionPrinter.ifPresent(printer -> printer.accept(slot));
		return this.scanner.nextLine();
	}

	@Override
	public void close()
	{
		this.scanner.close();
	}
}
