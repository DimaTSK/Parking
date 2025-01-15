package org.hofftech.parking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Parcel {
    private String name;
    private List<String> shape;
    private char symbol;
    private ParcelStartPosition parcelStartPosition;

    /**
     * Возвращает ширину пакета, основанную на длине первой строки формы.
     *
     * @return Ширина пакета.
     */
    public int getWidth() {
        if (shape == null || shape.isEmpty()) {
            return 0;
        }
        return shape.get(0).length();
    }

    /**
     * Возвращает высоту пакета, основанную на количестве строк в форме.
     *
     * @return Высота пакета.
     */
    public int getHeight() {
        return (shape != null) ? shape.size() : 0;
    }

    /**
     * Обновляет символ, используемый в форме пакета.
     * Заменяет все вхождения текущего символа на новый символ.
     *
     * @param newSymbol Новый символ для использования в форме.
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
     * Возвращает перевёрнутую форму пакета.
     * Порядок строк в форме инвертируется.
     *
     * @return Перевёрнутая форма как список строк.
     */
    public List<String> getUniqueShape() {
        List<String> reversedShape = new ArrayList<>(this.shape);
        Collections.reverse(reversedShape);
        return reversedShape;
    }
}
