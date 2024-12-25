package org.hofftech.parking.model.enums;

public enum CommandConstants {
    EXIT_COMMAND("exit"),
    OUTPUT_DIRECTORY ( "out"),
    OUTPUT_TXT("out/input.txt");

    private final String value;

    CommandConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}