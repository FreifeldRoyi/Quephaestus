package com.freifeld.tools.quephaestus;

import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusMainTest
class ListElementsCommandTest {

    private static LauncherParamsBuilder createLaunchParams() {
        return new LauncherParamsBuilder().withSubCommandName("list-elements");
    }

    @Test
    public void testListElementsCommand(QuarkusMainLauncher launcher) {
        var args = createLaunchParams().withConfigPath().build();
        var result = launcher.launch(args);
        assertEquals(0, result.exitCode());
        assertEquals(
                "Possible element names are: [resource, resource-mapper, orchestrator, repository, entity, create-command]",
                result.getOutput());
    }

}