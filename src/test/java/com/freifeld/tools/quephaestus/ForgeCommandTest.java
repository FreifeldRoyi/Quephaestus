package com.freifeld.tools.quephaestus;

import com.freifeld.tools.quephaestus.commands.ForgeCommand;
import com.freifeld.tools.quephaestus.messages.ExceptionMessageTemplates;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusMainTest
class ForgeCommandTest {
    private static LauncherParamsBuilder createLaunchParams() {
        return new LauncherParamsBuilder().withSubCommandName("forge");
    }

    @Test
    public void testForgeCommand_elementNameDoesNotExist(QuarkusMainLauncher launcher) {
        var element = "controller";
        var launchParams = createLaunchParams().withConfigPath().withElement(element).withModule("my-controller");
        var buildArgs = launchParams.build();
        var result = launcher.launch(buildArgs);
        assertEquals(2, result.exitCode());
        assertTrue(result.getErrorOutput().startsWith(ExceptionMessageTemplates.INVALID_ELEMENT.formatted(
                element,
                "[resource, resource-mapper, orchestrator, repository, entity, create-command]",
                launchParams.configFilePath.getPath())));
    }

//    @Test
//    public void testForge_withInteractive() {
//        final var blacksmith = Mockito.mock(Blacksmith.class);
//        final var command = new ForgeCommand();
//        command.setBlacksmith(blacksmith);
//
//        final var cmd = new CommandLine(command);
//
//
//        var element = "resource";
//        var args = createLaunchParams().withConfigPath().withElement(element).withModule("my-resource").interactive().build();
//        var result = cmd.execute(args);
//        assertEquals(0, result);
//        assertEquals(
//                "Possible element names are: [resource, resource-mapper, orchestrator, repository, entity, create-command]",
//                cmd.getOut().toString());
//    }

}