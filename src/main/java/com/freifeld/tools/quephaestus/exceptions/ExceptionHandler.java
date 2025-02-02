package com.freifeld.tools.quephaestus.exceptions;

import jakarta.enterprise.context.ApplicationScoped;
import picocli.CommandLine;

import java.io.PrintWriter;

@ApplicationScoped
public class ExceptionHandler implements CommandLine.IParameterExceptionHandler, CommandLine.IExecutionExceptionHandler {

    private int handleException(Exception exception, PrintWriter out, CommandLine.Help.ColorScheme colorScheme) {
        var message = exception.getMessage();
        if (exception.getCause() != null && exception.getCause() instanceof QuephaestusException ex) {
            message = ex.getMessage();
        }

        out.println(colorScheme.text(message));
        return 2;
    }

    @Override
    public int handleParseException(CommandLine.ParameterException exception, String[] args) throws Exception {
        return this.handleException(exception, exception.getCommandLine().getErr(), exception.getCommandLine().getColorScheme());
    }

    @Override
    public int handleExecutionException(Exception exception, CommandLine commandLine, CommandLine.ParseResult fullParseResult) throws Exception {

        if (exception instanceof QuephaestusException ex) {
            return this.handleException(exception, commandLine.getErr(), commandLine.getColorScheme());
        }

        throw exception;
    }

}
