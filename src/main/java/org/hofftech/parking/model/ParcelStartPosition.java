package org.hofftech.parking.model;
/**
 * Класс, представляющий начальную позицию посылки в пространстве.
 * Содержит координаты по осям X и Y.
 *
 * @param x координата X позиции посылки.
 * @param y координата Y позиции посылки.
 */
public record ParcelStartPosition(int x, int y) {
}