package com.freifeld.tools.quephaestus.core.deployment;

import com.freifeld.tools.quephaestus.core.Blacksmith;
import com.freifeld.tools.quephaestus.core.Forge;
import com.freifeld.tools.quephaestus.core.configuration.Blueprint;
import com.freifeld.tools.quephaestus.core.configuration.Element;
import com.freifeld.tools.quephaestus.core.configuration.ElementGroup;
import com.freifeld.tools.quephaestus.core.configuration.QuephaestusConfiguration;
import com.freifeld.tools.quephaestus.core.scripting.ScriptRunner;
import com.freifeld.tools.quephaestus.core.templateExtensions.StringTemplateExtensions;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

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
                Blacksmith.class,
                Forge.class
        );
    }

    @BuildStep
    ReflectiveClassBuildItem reflection() {
        return ReflectiveClassBuildItem.builder(
                        QuephaestusConfiguration.class,
                        Element.class,
                        ElementGroup.class,
                        Blueprint.class)
                .queryMethods()
                .build();
    }
}