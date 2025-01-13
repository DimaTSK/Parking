package org.hofftech.parking.model;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class Truck {
    private final int width;
    private final int height;

    private final char[][] grid;
    private final List<Parcel> parcels;

    public Truck(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new char[height][width];
        this.parcels = new ArrayList<>();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = ' ';
            }
        }
    }
}