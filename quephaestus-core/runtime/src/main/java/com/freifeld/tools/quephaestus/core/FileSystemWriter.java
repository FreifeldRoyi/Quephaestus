package com.freifeld.tools.quephaestus.core;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
public class FileSystemWriter
{
	public void writeContent(Path outputPath, String renderedTemplate) throws IOException
	{
		var path = Files.writeString(outputPath, renderedTemplate);
	}
}
