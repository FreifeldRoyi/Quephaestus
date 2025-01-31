package com.freifeld.tools.quephaestus;

import com.freifeld.tools.quephaestus.commands.EntryCommand;
import com.freifeld.tools.quephaestus.exceptions.ExceptionHandler;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import picocli.CommandLine;

@QuarkusMain
public class Main implements QuarkusApplication {

    @Inject
    CommandLine.IFactory factory;

    @Override
    public int run(String... args) throws Exception {
        final var commandLine = new CommandLine(new EntryCommand(), factory);
        commandLine.setParameterExceptionHandler(new ExceptionHandler());
        return commandLine.execute(args);
    }
}
