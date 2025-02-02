package com.freifeld.tools.quephaestus.mixins;

import jakarta.enterprise.context.ApplicationScoped;
import picocli.CommandLine.Option;

@ApplicationScoped
public class InteractiveModeMixin {

    private boolean interactive;

    public boolean isInteractive() {
        return this.interactive;
    }

    @Option(names = {"-i", "--interactive"}, arity = "0..1", defaultValue = "false")
    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

}
