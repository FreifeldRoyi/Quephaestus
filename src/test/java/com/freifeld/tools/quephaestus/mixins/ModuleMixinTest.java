package com.freifeld.tools.quephaestus.mixins;

import com.freifeld.tools.quephaestus.commands.EntryCommand;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import static com.freifeld.tools.quephaestus.messages.ExceptionMessageTemplates.MODULE_NOT_DEFINED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class ModuleMixinTest
{
	public static Stream<ModuleMixin> cutProvider()
	{
		var entry = new EntryCommand();
		var cmd = new CommandLine(entry);
		var cut = new ModuleMixin();
		cut.setCommandSpec(cmd.getCommandSpec());

		return Stream.of(cut);
	}

	@ParameterizedTest
	@MethodSource("cutProvider")
	public void testSetModule_simpleModuleName(ModuleMixin cut) throws IOException
	{
		final var moduleName = "my-moduleName";
		cut.setModule(moduleName);
		assertEquals(moduleName, cut.moduleName());
		assertEquals(Path.of(moduleName), cut.modulePath());
	}

	@ParameterizedTest
	@MethodSource("cutProvider")
	public void testSetModule_complexModuleName(ModuleMixin cut) throws IOException
	{
		final var modulePath = "long.path";
		final var moduleName = "moduleName";
		final var full = modulePath + "." + moduleName;
		cut.setModule(full);
		assertEquals(moduleName, cut.moduleName());
		assertEquals(Path.of("long/path/moduleName"), cut.modulePath());
	}

	@ParameterizedTest
	@MethodSource("cutProvider")
	public void testSetModule_emptyModule(ModuleMixin cut) throws IOException
	{
		assertThrowsExactly(
				CommandLine.ParameterException.class,
				() -> cut.setModule("       \t\n "),
				MODULE_NOT_DEFINED);
		assertThrowsExactly(CommandLine.ParameterException.class, () -> cut.setModule(null), MODULE_NOT_DEFINED);
	}
}