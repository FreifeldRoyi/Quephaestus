package com.freifeld.tools.quephaestus;

import com.freifeld.tools.quephaestus.messages.ExceptionMessageTemplates;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static com.freifeld.tools.quephaestus.messages.ExceptionMessageTemplates.PATH_ALREADY_EXISTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusMainTest
class ForgeCommandTest
{
	private static LauncherParamsBuilder createLaunchParams()
	{
		return new LauncherParamsBuilder().withSubCommandName("forge");
	}

	@Test
	public void testForgeCommand_elementNameDoesNotExist(QuarkusMainLauncher launcher)
	{
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

	@Test
	public void testGenerate_moduleAlreadyExists_emptyDir(QuarkusMainLauncher launcher) throws IOException
	{
		var dirName = "my-temp-dir";
		var dir = Files.createTempDirectory(dirName);
		var buildArgs = createLaunchParams().withConfigPath()
		                                    .withModule(dir.toString())
		                                    .withElement("resource")
		                                    .build();
		var result = launcher.launch(buildArgs);
		assertEquals(0, result.exitCode());
	}

	@Test
	public void testGenerate_moduleAlreadyExists_fileExists(QuarkusMainLauncher launcher) throws IOException
	{
		var fileName = "my-temp-file";
		var path = Files.createTempFile(fileName, "");
		var buildArgs = createLaunchParams().withConfigPath()
		                                    .withModule(path.toString())
		                                    .withElement("resource")
		                                    .build();
		var result = launcher.launch(buildArgs);
		assertEquals(2, result.exitCode());
		assertTrue(result.getErrorOutput().startsWith(PATH_ALREADY_EXISTS.formatted(path)));
	}
}