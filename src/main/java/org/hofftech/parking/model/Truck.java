package org.hofftech.parking.model;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


/**
 * Класс, представляющий грузовик, используемый для упаковки посылок.
 * Хранит информацию о размерах грузовика, его сетке и списке посылок.
 */
@Getter
@ToString
public class Truck {
    private static final int SHAPE_FIRST_INDEX = 0;
    private final int width;
    private final int height;
    private final char[][] grid;
    private final List<Parcel> parcels;

    /**
     * Конструктор грузовика с указанной шириной и высотой.
     * Инициализирует сетку пустыми пробелами и создаёт пустой список посылок.
     *
     * @param width  ширина грузовика
     * @param height высота грузовика
     */
    public Truck(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new char[height][width];
        this.parcels = new ArrayList<>();

        for (int i = SHAPE_FIRST_INDEX; i < height; i++) {
            for (int j = SHAPE_FIRST_INDEX; j < width; j++) {
                grid[i][j] = ' ';
            }
        }
    }
}
