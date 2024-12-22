package org.hofftech.parking.model;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class Truck {
    private final int WIDTH = 6;
    private final int HEIGHT = 6;

    private char[][] grid;
    private final List<Package> packages;

    public Truck() {
        this.grid = new char[HEIGHT][WIDTH];
        this.packages = new ArrayList<>();
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                grid[i][j] = ' ';
            }
        }
    }
}