package com.freifeld.tools.quephaestus.exceptions;

import java.util.Set;

public class MissingDataException extends Exception
{
	private Set<String> missing;
	public MissingDataException(Set<String> missing) {
		this.missing = missing;
	}

	public Set<String> missingData()
	{
		return missing;
	}
}
