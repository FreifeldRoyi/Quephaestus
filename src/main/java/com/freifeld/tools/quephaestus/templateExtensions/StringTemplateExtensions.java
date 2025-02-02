package com.freifeld.tools.quephaestus.templateExtensions;

import io.quarkus.qute.TemplateExtension;

import java.util.regex.Pattern;

@TemplateExtension
public class StringTemplateExtensions
{
	public static String toPascalCase(String value)
	{
		final var pattern = Pattern.compile("([0-9a-zA-Z]+)");
		final var matcher = pattern.matcher(value);

		final var sb = new StringBuilder();
		while (matcher.find())
		{
			final var group = matcher.group(1);
			var firstLowerCase = true;
			for (var letter : group.toCharArray())
			{
				var letterToAppend = letter;
				if (firstLowerCase && Character.isAlphabetic(letter))
				{
					letterToAppend = Character.toUpperCase(letter);
					firstLowerCase = false;
				}
				sb.append((char) letterToAppend);
			}
		}
		return sb.toString();
	}
}
