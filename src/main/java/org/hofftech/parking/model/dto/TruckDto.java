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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("+");
        sb.append("-".repeat(grid[0].length));
        sb.append("+\n");

        for (int i = grid.length - 1; i >= 0; i--) {
            sb.append("|");
            for (int j = 0; j < grid[i].length; j++) {
                sb.append(grid[i][j]);
            }
            sb.append("|\n");
        }
        sb.append("+");
        sb.append("-".repeat(grid[0].length));
        sb.append("+");

        return sb.toString();
    }
}