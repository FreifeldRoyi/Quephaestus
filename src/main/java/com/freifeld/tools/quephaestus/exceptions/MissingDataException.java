package com.freifeld.tools.quephaestus.exceptions;

import java.util.Set;

public class MissingDataException extends RuntimeException
{
	private final Set<String> missing;
	public MissingDataException(Set<String> missing) {
		this.missing = missing;
	}

	public Set<String> missingData()
	{
		return missing;
	}
}
