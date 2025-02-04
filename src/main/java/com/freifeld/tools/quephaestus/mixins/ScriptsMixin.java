package com.freifeld.tools.quephaestus.mixins;

import jakarta.enterprise.context.ApplicationScoped;
import picocli.CommandLine.Option;

import java.util.List;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

@ApplicationScoped
public class ScriptsMixin {

    private List<String> preForge;
    private List<String> postForge;

    @Option(names = {"--pre-forge"},
            description = "A command/script to execute before the forge starts",
            arity = "0..1",
            required = false)
    public void setPreForge(String path) {
        this.preForge = pathSplitter(path);
    }

    @Option(names = {"--post-forge"},
            description = "A command/script to execute after the forge ends",
            arity = "0..1",
            required = false)
    public void setPostForge(String path) {
        this.postForge = pathSplitter(path);
    }

    public static List<String> pathSplitter(String path) {
        final var pattern = Pattern.compile("\"(.*)\"|'(.*)'|(\\S+)");
        final var matcher = pattern.matcher(path);
        return matcher.results().map(MatchResult::group).toList();
    }

    public Optional<List<String>> preForge() {
        return this.filterEmpty(this.preForge);
    }

    public Optional<List<String>> postForge() {
        return this.filterEmpty(this.postForge);
    }

    private Optional<List<String>> filterEmpty(List<String> scriptParts) {
        return Optional.ofNullable(scriptParts).filter(parts -> !parts.isEmpty());
    }

}
