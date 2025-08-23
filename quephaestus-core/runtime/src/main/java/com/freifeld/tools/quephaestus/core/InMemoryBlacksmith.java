package com.freifeld.tools.quephaestus.core;

import com.freifeld.tools.quephaestus.core.configuration.Blueprint;

public abstract class InMemoryBlacksmith<T> extends Blacksmith<T> {

    public InMemoryBlacksmith(Forge forge, TemplateResolver<T> templateResolver) {
        super(forge, templateResolver);
    }

    @Override
    protected void validateBlueprint(Blueprint<T> blueprint) {
        super.validateBlueprint(blueprint);
        Validators.validateRelativePath(blueprint.workingDir());
        Validators.validateRelativePath(blueprint.baseDir());
        Validators.validateRelativePath(blueprint.modulePath());
    }
}
