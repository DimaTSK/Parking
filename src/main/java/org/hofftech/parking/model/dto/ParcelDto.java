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
        log.info(String.format("Создан объект Parcel с шириной: %d и высотой: %d", getWidth(), getHeight()));
    }

    public int getWidth() {
        return lines[0].length();
    }

    public int getHeight() {
        return lines.length;
    }
}