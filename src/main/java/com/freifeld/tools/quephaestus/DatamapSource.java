package com.freifeld.tools.quephaestus;

import java.util.Map;
import java.util.Set;

public interface DatamapSource
{
	Set<String> COMMON_TEMPLATE_PARAMETERS = Set.of("namespace", "projectName");

	Map<String,String> fillDataMap(Set<String> requiredParts);
}
