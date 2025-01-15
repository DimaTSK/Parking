package org.hofftech.parking.model;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс, представляющий грузовик, используемый для перевозки посылок.
 *
 * <p>Грузовик имеет определенные размеры (ширину и высоту) и сетку позиций,
 * в которой хранятся посылки. Также хранится список объектов {@link Parcel},
 * содержащихся в грузовике.</p>
 */
@Getter
@ToString
public class Truck {
    /**
     * Ширина грузовика.
     */
    private final int width;

    /**
     * Высота грузовика.
     */
    private final int height;

    /**
     * Сетчатая структура, представляющая положение посылок в грузовике.
     * Каждый элемент сетки представляет одну позицию, которая может быть
     * занята символом или пустым пространством.
     */
    private final char[][] grid;

    /**
     * Список посылок, находящихся в грузовике.
     */
    private final List<Parcel> parcels;

    /**
     * Создает новый экземпляр грузовика с заданной шириной и высотой.
     *
     * <p>Инициализирует сетку позиций пустыми символами и создает пустой список посылок.</p>
     *
     * @param width  ширина грузовика
     * @param height высота грузовика
     */
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
