package com.freifeld.tools.quephaestus.exceptions;

public class ModuleNameIsEmptyException extends HandledQuephaestusException {
    public static final String MESSAGE_FORMAT = "Module name cannot be null or empty";

    public ModuleNameIsEmptyException() {
        super(MESSAGE_FORMAT);
    }
}
