package org.hofftech.parking.model.dto;

import lombok.Getter;

import java.util.Arrays;

@Getter
public class TruckDto {
    private final char[][] grid;

    public TruckDto(int width, int height) {
        this.grid = new char[height][width];
        clear();
    }

    private void clear() {
        for (char[] chars : grid) {
            Arrays.fill(chars, ' ');
        }
    }
}