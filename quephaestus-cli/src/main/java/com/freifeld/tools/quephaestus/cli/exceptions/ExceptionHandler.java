package com.freifeld.tools.quephaestus.cli.exceptions;

import com.freifeld.tools.quephaestus.core.exceptions.HandledQuephaestusException;
import com.freifeld.tools.quephaestus.core.exceptions.QuephaestusException;
import com.freifeld.tools.quephaestus.core.exceptions.UnhandledQuephaestusException;
import jakarta.enterprise.context.ApplicationScoped;
import picocli.CommandLine;

@ApplicationScoped
public class ExceptionHandler implements CommandLine.IParameterExceptionHandler, CommandLine.IExecutionExceptionHandler {

    private int handleException(QuephaestusException exception, CommandLine cmd) {
        var message = switch (exception) {
            case HandledQuephaestusException ex -> ex.getMessage();
            case UnhandledQuephaestusException ex -> {
                CommandLine.tracer().debug("Unhandled exception", ex.getCause());
                yield ex.getMessage();
            }
            default -> {
                // Should not happen
                throw new IllegalStateException("Unexpected value: " + exception);
            }
        };

        final var output = cmd.getErr();
        final var colorScheme = cmd.getColorScheme();
        output.println(colorScheme.errorText(message));
//        output.println(colorScheme.stackTraceText(exception));

        return 2;
    }

    @Override
    public int handleParseException(CommandLine.ParameterException exception, String[] args) {
        final var commandLine = exception.getCommandLine();
        if ("Missing required subcommand".equals(exception.getMessage())) {
            commandLine.usage(commandLine.getOut(), commandLine.getColorScheme());
            return 2;
        }

        if (exception.getCause() instanceof QuephaestusException e) {
            return this.handleException(e, commandLine);
        }

        throw exception;
    }

    @Override
    public int handleExecutionException(Exception exception, CommandLine commandLine, CommandLine.ParseResult fullParseResult) throws Exception {
        if (exception instanceof QuephaestusException ex) {
            return this.handleException(ex, commandLine);
        }

        final var output = commandLine.getErr();
        final var colorScheme = commandLine.getColorScheme();

        CommandLine.tracer().debug("Unhandled exception", exception);
        output.println(colorScheme.errorText(exception.getMessage()));
        output.println(colorScheme.stackTraceText(exception));
        return 2;
    }

}
