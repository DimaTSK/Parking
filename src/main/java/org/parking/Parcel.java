package org.parking;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Parcel {
    @Getter
    private final String[] lines;
    private final ParcelValidator validator;

    public Parcel(String packageStr) {
        this.validator = new ParcelValidator();
        validator.validate(packageStr);  // Вызов функции валидации
        this.lines = packageStr.split("\n");
        log.info("Создан объект Parcel с шириной: " + getWidth() + " и высотой: " + getHeight());
    }

    public int getWidth() {
        return lines[0].length();
    }

    public int getHeight() {
        return lines.length;
    }

}