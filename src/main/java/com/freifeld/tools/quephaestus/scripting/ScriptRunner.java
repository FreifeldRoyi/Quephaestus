package com.freifeld.tools.quephaestus.scripting;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@ApplicationScoped
public class ScriptRunner {

    public int scriptRunner(Path workingDir, List<String> scriptParts) {
        try {
            final var process = new ProcessBuilder(scriptParts)
                    .directory(workingDir.toFile())
                    .inheritIO()
                    .start();
            var code = process.waitFor();

            if (code != 0) {
                throw new RuntimeException("Expected result did not");
            }

            return code;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e); // TODO
        }
    }

}
