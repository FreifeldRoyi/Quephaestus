package com.freifeld.tools.quephaestus.core.configuration;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record Element(String path, String namePattern)
{ }
