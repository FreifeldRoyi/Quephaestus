package com.freifeld.tools.quephaestus.core.deployment;

import com.freifeld.tools.quephaestus.core.Blacksmith;
import com.freifeld.tools.quephaestus.core.FileSystemWriter;
import com.freifeld.tools.quephaestus.core.Forge;
import com.freifeld.tools.quephaestus.core.StdIOProvider;
import com.freifeld.tools.quephaestus.core.scripting.ScriptRunner;

import com.freifeld.tools.quephaestus.core.templateExtensions.StringTemplateExtensions;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class ExtensionProcessor {
    private static final String FEATURE = "quephaestus-core";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    AdditionalBeanBuildItem registerClasses() {
        return new AdditionalBeanBuildItem(
                StringTemplateExtensions.class,
                ScriptRunner.class,
                StdIOProvider.class,
                FileSystemWriter.class,
                Blacksmith.class,
                Forge.class
        );
    }
}