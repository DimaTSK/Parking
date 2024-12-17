package org.parking.model.dto;

public class TruckDto {
    private final char[][] grid;

    public TruckDto(int width, int height) {
        this.grid = new char[height][width];
        clear();
    }

    private void clear() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = ' ';
            }
        }
    }

    public char[][] getGrid() {
        return grid;
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

    public void print() {
        System.out.print("+");
        for (int j = 0; j < grid[0].length; j++) {
            System.out.print("-");
        }
        System.out.println("+");

        for (int i = grid.length - 1; i >= 0; i--) {
            System.out.print("|");
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j]);
            }
            System.out.println("|");
        }

        System.out.print("+");
        for (int j = 0; j < grid[0].length; j++) {
            System.out.print("-");
        }
        System.out.println("+");
    }
}