package com.freifeld.tools.quephaestus.exceptions;

import jakarta.enterprise.context.ApplicationScoped;
import picocli.CommandLine;
import picocli.CommandLine.Help.Ansi;

import static picocli.CommandLine.Help.Ansi.AUTO;

@ApplicationScoped
public class ExceptionHandler implements CommandLine.IParameterExceptionHandler, CommandLine.IExecutionExceptionHandler {

    public Ansi.Text toExceptionStyle(String message) {
        return AUTO.text("@|fg(red) %s|@".formatted(message));
    }

    @Override
    public int handleParseException(CommandLine.ParameterException exception, String[] args) throws Exception {
        switch (exception.getCause()) {
            case QuephaestusException ex -> {
                final var message = this.toExceptionStyle(ex.getMessage());
                exception.getCommandLine().getOut().println(message);
            }
            default -> throw exception;
        }

        return 2;
    }

    @Override
    public int handleExecutionException(Exception exception, CommandLine commandLine, CommandLine.ParseResult fullParseResult) throws Exception {
        switch (exception) {
            case QuephaestusException ex -> {
                final var message = this.toExceptionStyle(ex.getMessage());
                commandLine.getOut().println(message);
            }
            default -> throw exception;
        }

        return 2;
    }

}
