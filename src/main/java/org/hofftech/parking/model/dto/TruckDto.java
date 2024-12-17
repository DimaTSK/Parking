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

    public boolean canPlaceParcel(ParcelDto parcel, int startRow, int startCol) {
        if (startRow < 0 || startCol < 0 || startRow + parcel.getHeight() > grid.length || startCol + parcel.getWidth() > grid[0].length) {
            return false;
        }

        for (int i = 0; i < parcel.getHeight(); i++) {
            for (int j = 0; j < parcel.getWidth(); j++) {
                if (grid[startRow + i][startCol + j] != ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    public void placeParcel(ParcelDto parcel, int startRow, int startCol) {
        if (!canPlaceParcel(parcel, startRow, startCol)) {
            throw new IllegalArgumentException("Не удается разместить посылку на указанной позиции.");
        }

        for (int i = 0; i < parcel.getHeight(); i++) {
            for (int j = 0; j < parcel.getWidth(); j++) {
                grid[startRow + i][startCol + j] = parcel.getLines()[i].charAt(j);
            }
        }
    }
}