package org.hofftech.parking.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hofftech.parking.model.enums.OrderOperationType;

import java.time.LocalDate;
import java.util.List;

/**
 * Класс {@code Order} представляет собой заказ, содержащий информацию о пользователе, дате, типе операции,
 * количестве грузовиков и списке посылок.
 *
 * Использует аннотации {@link Data} для автоматического создания геттеров, сеттеров, методов {@code toString},
 * {@code equals} и {@code hashCode}, а также аннотацию {@link RequiredArgsConstructor} для создания конструктора
 * только с обязательными полями (final).
 */
@Data
@RequiredArgsConstructor
public class Order {

    private static final int SPACE_CHAR = ' ';

    private final String userId;
    private final LocalDate date;
    private final OrderOperationType operationType;
    private final int truckCount;
    private final List<Parcel> parcels;

    /**
     * Подсчитывает количество сегментов в форме посылки.
     * Сегментом считается любой символ, кроме пробела.
     *
     * @param shape список строк, представляющих форму посылки
     * @return количество сегментов в форме посылки
     */
    public int countSegments(List<String> shape) {
        return shape.stream()
                .mapToInt(row -> (int) row.chars().filter(ch -> ch != SPACE_CHAR).count())
                .sum();
    }
}
