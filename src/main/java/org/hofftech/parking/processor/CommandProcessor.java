package org.hofftech.parking.processor;

import org.hofftech.parking.model.ParsedCommand;

public interface CommandProcessor {
    void execute(ParsedCommand command);
}