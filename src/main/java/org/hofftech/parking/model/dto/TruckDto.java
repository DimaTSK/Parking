package org.hofftech.parking.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class TruckDto {
    private final int WIDTH = 6;
    private final int HEIGHT = 6;

    private char[][] grid;
    private final List<ParcelDto> parcelDtos;

    public TruckDto() {
        this.grid = new char[HEIGHT][WIDTH];
        this.parcelDtos = new ArrayList<>();
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                grid[i][j] = ' ';
            }
        }
    }
}