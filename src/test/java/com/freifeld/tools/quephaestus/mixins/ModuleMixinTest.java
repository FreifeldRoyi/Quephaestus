package com.freifeld.tools.quephaestus.mixins;

import com.freifeld.tools.quephaestus.commands.EntryCommand;
import com.freifeld.tools.quephaestus.exceptions.ModuleNameIsEmptyException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class ModuleMixinTest {
    public static Stream<ModuleMixin> cutProvider() {
        var entry = new EntryCommand();
        var cmd = new CommandLine(entry);
        var cut = new ModuleMixin();
        cut.setCommandSpec(cmd.getCommandSpec());

        return Stream.of(cut);
    }

    @ParameterizedTest
    @MethodSource("cutProvider")
    public void testSetModule_simpleModuleName(ModuleMixin cut) throws IOException {
        final var moduleName = "my-moduleName";
        cut.setModule(moduleName);
        assertEquals(moduleName, cut.moduleName());
        assertEquals(Path.of(moduleName), cut.modulePath());
    }

    @ParameterizedTest
    @MethodSource("cutProvider")
    public void testSetModule_complexModuleName(ModuleMixin cut) throws IOException {
        final var modulePath = "long.path";
        final var moduleName = "moduleName";
        final var full = modulePath + "." + moduleName;
        cut.setModule(full);
        assertEquals(moduleName, cut.moduleName());
        assertEquals(Path.of("long/path/moduleName"), cut.modulePath());
    }

    @ParameterizedTest
    @MethodSource("cutProvider")
    public void testSetModule_emptyModule(ModuleMixin cut) {
        assertThrowsExactly(ModuleNameIsEmptyException.class, () -> cut.setModule("       \t\n "));
        assertThrowsExactly(ModuleNameIsEmptyException.class, () -> cut.setModule(null));
    }
}