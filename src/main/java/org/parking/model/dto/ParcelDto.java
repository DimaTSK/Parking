package org.parking.model.dto;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.parking.utill.ParcelValidator;

@Slf4j
public class ParcelDto {
    @Getter
    private final String[] lines;
    private final ParcelValidator validator;

    public ParcelDto(String packageStr) {
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