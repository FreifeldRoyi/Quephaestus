package com.freifeld.tools.quephaestus.core.scripting;

import com.freifeld.tools.quephaestus.core.exceptions.ScriptRunFailedException;
import com.freifeld.tools.quephaestus.core.exceptions.UnhandledQuephaestusException;
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
                throw new ScriptRunFailedException(code, String.join(" ", scriptParts));
            }

            return code;
        } catch (IOException | InterruptedException e) {
            throw new UnhandledQuephaestusException("Script execution terminated unsuccessfully", e);
        }
    }

}
