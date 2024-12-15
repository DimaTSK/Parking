package org.parking.utill;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParcelValidator {
    public void validate(String packageStr) {
        if (packageStr == null || packageStr.trim().isEmpty()) {
            log.error("Входная строка не должна быть пустой или null.");
            throw new IllegalArgumentException("Входная строка не должна быть пустой или null.");
        }

        String[] linesArray = packageStr.split("\n");
        if (linesArray.length == 0) {
            log.error("Входная строка должна содержать хотя бы одну линию.");
            throw new IllegalArgumentException("Входная строка должна содержать хотя бы одну линию.");
        }

        for (String line : linesArray) {
            if (line.length() == 0) {
                log.error("Каждая линия в строке пакета должна содержать хотя бы один символ.");
                throw new IllegalArgumentException("Каждая линия в строке пакета должна содержать хотя бы один символ.");
            }
        }
    }
}