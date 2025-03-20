package com.freifeld.tools.quephaestus.core;

import io.quarkus.qute.Template;

public record ForgeMaterial(String elementName, Template template, Template filename, Template elementPath) {
}
