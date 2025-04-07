package com.freifeld.tools.quephaestus.core;

import io.quarkus.qute.Engine;
import io.quarkus.qute.Expression;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class Forge {
    private final Engine engine;

    public Forge(Engine engine) {
        this.engine = engine;
    }

    public Template parse(String content) {
        return this.engine.parse(content);
    }

    public String render(Template template, Map<String, String> data) {
        final var templateInstance = template.instance();

        return this.interpolationSlotsFrom(template)
                .distinct()
                .map(slot -> templateInstance.data(slot, data.get(slot)))
                .reduce((_, t2) -> t2) // get last. Will work only on ordered stream
                .map(TemplateInstance::render)
                .orElseGet(templateInstance::render);
    }

    /*
     * This will be the only place that can extract expressions from template.
     *  by doing that, I contain Qute's function handling to a single place
     */
    public Stream<String> interpolationSlotsFrom(Template template) {
        return template.getExpressions()
                .stream()
                .filter(Predicate.not(Expression::isLiteral))
                .map(expression -> expression.getParts().getFirst().getName());
    }
}
