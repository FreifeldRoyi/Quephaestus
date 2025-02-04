package com.freifeld.tools.quephaestus;

import com.freifeld.tools.quephaestus.commands.EntryCommand;
import com.freifeld.tools.quephaestus.exceptions.ExceptionHandler;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.io.PrintWriter;

@QuarkusMain
public class Main implements QuarkusApplication {

    @Inject
    CommandLine.IFactory factory;

    @Inject
    ExceptionHandler exceptionHandler;

    @Inject
    PrintWriter writer;

    @Override
    public int run(String... args) throws Exception {
        final var commandLine = new CommandLine(new EntryCommand(), factory);
        commandLine.setParameterExceptionHandler(this.exceptionHandler);
        commandLine.setExecutionExceptionHandler(this.exceptionHandler);
        commandLine.setOut(this.writer);
        return commandLine.execute(args);
    }
}
