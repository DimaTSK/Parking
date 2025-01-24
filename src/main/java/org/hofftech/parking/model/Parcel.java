package org.hofftech.parking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Класс {@code Parcel} представляет собой посылку с определенными свойствами, такими как название, форма, символ и начальная позиция.
 * Реализует интерфейс {@link Comparable} для сравнения посылок по высоте и ширине.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Parcel implements Comparable<Parcel> {
    private String name;
    private List<String> shape;
    private char symbol;
    private ParcelStartPosition parcelStartPosition;
    private static final int START_POSITION_HEIGHT = 0;

    public int getWidth() {
        return shape.getFirst().length();
    }

    public int getHeight() {
        return shape.size();
    }

    /**
     * Обновляет символ, используемый для представления посылки, и заменяет все вхождения старого символа на новый в форме.
     *
     * @param newSymbol новый символ для замены
     */
    public void updateSymbol(char newSymbol) {
        if (shape == null || shape.isEmpty()) {
            return;
        }

        List<String> updatedShape = new ArrayList<>();
        for (String row : shape) {
            String updatedRow = row.replace(symbol, newSymbol);
            updatedShape.add(updatedRow);
        }
        this.shape = updatedShape;
        this.symbol = newSymbol;
    }

    /**
     * Возвращает форму посылки в обратном порядке строк.
     *
     * @return список строк формы в обратном порядке
     */
    public List<String> getReversedShape() {
        List<String> reversedShape = new ArrayList<>(this.shape);
        Collections.reverse(reversedShape);
        return reversedShape;
    }

    /**
     * Сравнивает текущую посылку с другой по высоте и ширине.
     * Сначала сравнивается высота в порядке убывания, затем ширина в порядке убывания, если высота равна.
     *
     * @param other другая посылка для сравнения
     * @return отрицательное целое число, ноль или положительное целое число, если текущая посылка меньше, равна или больше другой
     */
    @Override
    public int compareTo(Parcel other) {
        int heightDiff = Integer.compare(other.getHeight(), this.getHeight());
        if (heightDiff == START_POSITION_HEIGHT) {
            return Integer.compare(other.getWidth(), this.getWidth());
        }
        return heightDiff;
    }
}