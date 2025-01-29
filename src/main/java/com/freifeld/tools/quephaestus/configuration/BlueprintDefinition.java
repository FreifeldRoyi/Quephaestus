package com.freifeld.tools.quephaestus.configuration;

import java.util.Map;
import java.util.Set;

public record BlueprintDefinition(Set<String> elements, Map<String, String> mappings)
{ }
