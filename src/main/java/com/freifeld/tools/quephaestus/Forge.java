package com.freifeld.tools.quephaestus;

import com.freifeld.tools.quephaestus.exceptions.UnhandledQuephaestusException;
import io.quarkus.qute.Engine;
import io.quarkus.qute.Expression;
import io.quarkus.qute.Template;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@ApplicationScoped
public class Forge {
    @Inject
    private Engine engine;

    public Template parse(Path templatePath) {
        try {
            String content = Files.readString(templatePath);
            return this.parse(content);
        } catch (IOException e) {
            throw new UnhandledQuephaestusException("Failed to parse template [%s]".formatted(templatePath), e);
        }
    }

    public Template parse(String content) {
        return this.engine.parse(content);
    }

    public String render(Template template, Map<String, String> data) {
        var templateInstance = template.instance();
        for (var slot : this.interpolationSlotsFrom(template)) {
            templateInstance = templateInstance.data(slot, data.get(slot));
        }

        return templateInstance.render();
    }

    /*
     * This will be the only place that can extract expressions from template.
     *  by doing that, I contain Qute's function handling to a single place
     */
    public Set<String> interpolationSlotsFrom(Template template) {
        return template.getExpressions()
                .stream()
                .filter(Predicate.not(Expression::isLiteral))
                .map(expression -> expression.getParts().getFirst().getName())
                .collect(Collectors.toSet());
    }
}
