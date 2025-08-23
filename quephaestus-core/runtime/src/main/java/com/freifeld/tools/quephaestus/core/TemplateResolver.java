package com.freifeld.tools.quephaestus.core;

public interface TemplateResolver<T> {
    String resolve(T templateData);
}
