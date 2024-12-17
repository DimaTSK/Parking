package org.hofftech.parking.model.dto;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.utill.ParcelValidator;

@Slf4j
public class ParcelDto {
    @Getter
    private final String[] lines;

    public ParcelDto(String[] lines) {
        this.lines = lines;
        log.info("Создан объект Parcel с шириной: " + getWidth() + " и высотой: " + getHeight());
    }

    public int getWidth() {
        return lines[0].length();
    }

    public int getHeight() {
        return lines.length;
    }

    public static ParcelDto create(String packageStr) {
        ParcelValidator validator = new ParcelValidator();
        validator.validate(packageStr);
        String[] lines = packageStr.split("\n");
        return new ParcelDto(lines);
    }
}