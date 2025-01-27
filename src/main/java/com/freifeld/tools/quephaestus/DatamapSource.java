package com.freifeld.tools.quephaestus;

import java.util.Map;
import java.util.Set;

public interface DatamapSource
{
	Map<String,String> fillDataMap(Set<String> requiredParts);
}
