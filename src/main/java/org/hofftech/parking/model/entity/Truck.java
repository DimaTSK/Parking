package org.hofftech.parking.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hofftech.parking.model.dto.ParcelDto;

import java.util.ArrayList;
import java.util.List;

@Setter
@ToString
public class Truck {
    @Getter
    private static final int WIDTH = 6;
    @Getter
    private static final int HEIGHT = 6;
    @Getter
    private char[][] grid;
    @Getter
    private final List<ParcelDto> parcelDtos;

    public Truck() {
        this.parcelDtos = new ArrayList<>();
        initializeGrid();
    }

    private void initializeGrid() {
        this.grid = new char[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                grid[i][j] = ' ';
            }
        }
    }

}