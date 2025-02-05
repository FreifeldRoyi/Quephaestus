package com.freifeld.tools.quephaestus;

import com.freifeld.tools.quephaestus.exceptions.InvalidParameterForCommandException;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusMainTest
class ForgeCommandTest {
    private static LauncherParamsBuilder createLaunchParams() {
        return new LauncherParamsBuilder().withSubCommandName("forge");
    }

// TODO mocks and stuff
    //    @Test
//    public void testForgeCommand_elementNameDoesNotExist(QuarkusMainLauncher launcher) {
//        var element = "controller";
//        var launchParams = createLaunchParams().withConfigPath().withElement(element).withModule("my-controller");
//        var buildArgs = launchParams.build();
//        var result = launcher.launch(buildArgs);
//        assertEquals(2, result.exitCode());
//        assertTrue(result.getErrorOutput().startsWith(InvalidParameterForCommandException.MESSAGE_FORMAT.formatted(
//                element,
//                "[resource, resource-mapper, orchestrator, repository, entity, create-command]",
//                launchParams.configFilePath.getPath())));
//    }

}