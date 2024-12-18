package org.hofftech.parking.model.dto;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParcelDto {
    @Getter
    private final String[] lines;

    public ParcelDto(String[] lines) {
        this.lines = lines;
        log.info("Создан объект Parcel с шириной: {} и высотой: {}", getWidth(), getHeight());
    }

    public int getWidth() {
        return lines[0].length();
    }

    public int getHeight() {
        return lines.length;
    }
}