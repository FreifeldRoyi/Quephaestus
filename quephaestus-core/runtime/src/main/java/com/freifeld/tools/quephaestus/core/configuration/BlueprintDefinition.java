package com.freifeld.tools.quephaestus.core.configuration;

import java.util.Map;
import java.util.Set;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record BlueprintDefinition(Set<String> elements, Map<String, String> mappings)
{ }
