package com.freifeld.tools.quephaestus.mixins;

import com.freifeld.tools.quephaestus.commands.EntryCommand;
import com.freifeld.tools.quephaestus.exceptions.PathDoesNotExistException;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import picocli.CommandLine;
import picocli.CommandLine.ParameterException;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import static com.freifeld.tools.quephaestus.messages.ExceptionMessageTemplates.PATH_CANNOT_BE_ABSOLUTE_PATH;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusMainTest
class DirectoryMixinTest {
    public static Stream<DirectoryMixin> cutProvider() {
        var entry = new EntryCommand();
        var cmd = new CommandLine(entry);
        var cut = new DirectoryMixin();
        cut.setCommandSpec(cmd.getCommandSpec());

        return Stream.of(cut);
    }

    @ParameterizedTest
    @MethodSource("cutProvider")
    public void test_setWorkingDirectory_emptyPath(DirectoryMixin cut) {
        var path = Path.of("");
        cut.setWorkingDirectory(Optional.empty());
        assertEquals(cut.workingDirectory(), path.toAbsolutePath()); // Stored as absolute
    }

    @ParameterizedTest
    @MethodSource("cutProvider")
    public void test_setWorkingDirectory_nonExistentPath(DirectoryMixin cut) {
        assertThrowsExactly(
                PathDoesNotExistException.class,
                () -> cut.setWorkingDirectory(Optional.of("not-existing")));
    }

    @ParameterizedTest
    @MethodSource("cutProvider")
    public void test_setWorkingDirectory_absolutePath(DirectoryMixin cut) {
        var absolutePath = Path.of("").toAbsolutePath();
        cut.setWorkingDirectory(Optional.of(absolutePath.toString()));
        assertEquals(cut.workingDirectory(), absolutePath);
    }

    @ParameterizedTest
    @MethodSource("cutProvider")
    public void test_setWorkingDirectory_relativePathIsStoredAsAbsolutePath(DirectoryMixin cut) {
        var relativePath = Path.of("");
        cut.setWorkingDirectory(Optional.of(relativePath.toString()));
        assertEquals(cut.workingDirectory(), relativePath.toAbsolutePath());
    }


    @ParameterizedTest
    @MethodSource("cutProvider")
    public void test_setBaseDirectory_emptyPath(DirectoryMixin cut) {
        cut.setBaseDirectory(Optional.empty());
        assertEquals(cut.baseDirectory(), Path.of(""));
    }

    @ParameterizedTest
    @MethodSource("cutProvider")
    public void test_setBaseDirectory_nonExistentPath(DirectoryMixin cut) {
        var path = Path.of("not-existing");
        assertDoesNotThrow(() -> cut.setBaseDirectory(Optional.of(path.toString())));
        assertEquals(cut.baseDirectory(), path); // stored as is
    }

    @ParameterizedTest
    @MethodSource("cutProvider")
    public void test_setBaseDirectory_absolutePath(DirectoryMixin cut) {
        var absolutePath = Path.of("").toAbsolutePath();
        assertThrowsExactly(
                ParameterException.class,
                () -> cut.setBaseDirectory(Optional.of(absolutePath.toString())),
                PATH_CANNOT_BE_ABSOLUTE_PATH.formatted(absolutePath));
    }
}