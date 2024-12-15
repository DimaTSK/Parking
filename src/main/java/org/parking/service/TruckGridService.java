package org.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.parking.model.dto.ParcelDto;

import java.util.stream.IntStream;

@Slf4j
public class TruckGridService {
    private final char[][] grid;

    public TruckGridService(int width, int height) {
        this.grid = new char[height][width];
        IntStream.range(0, height).forEach(i -> IntStream.range(0, width).forEach(j -> grid[i][j] = ' '));
    }

    public boolean canPlacePackage(ParcelDto pkg, int startRow, int startCol) {
        log.debug("Проверка размещения пакета на позиции: ({}, {})", startRow, startCol);
        for (int i = 0; i < pkg.getHeight(); i++) {
            for (int j = 0; j < pkg.getWidth(); j++) {
                if (grid[startRow + i][startCol + j] != ' ') {
                    return false; // Обнаружено занятое место
                }
            }
        }
        return true;
    }

    public void placePackage(ParcelDto pkg, int startRow, int startCol) {
        for (int i = 0; i < pkg.getHeight(); i++) {
            for (int j = 0; j < pkg.getWidth(); j++) {
                grid[startRow + i][startCol + j] = pkg.getLines()[i].charAt(j);
            }
        }
    }

    public void print() {
        System.out.print("+");
        IntStream.range(0, grid[0].length).forEach(j -> System.out.print("+"));
        System.out.println("+");

        IntStream.range(0, grid.length).mapToObj(i -> grid[grid.length - 1 - i]).forEach(row -> {
            System.out.print("+");
            IntStream.range(0, row.length).forEach(j -> System.out.print(row[j]));
            System.out.println("+");
        });

        System.out.print("+");
        IntStream.range(0, grid[0].length).forEach(j -> System.out.print("+"));
        System.out.println("+");
    }

    public char[][] getGrid() {
        return grid;
    }
}