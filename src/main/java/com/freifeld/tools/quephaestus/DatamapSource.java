package com.freifeld.tools.quephaestus;

import java.util.Map;
import java.util.Set;

public interface DatamapSource
{
	void fillDataMap(Map<String, String> datasource, Set<String> requiredParts);
}
