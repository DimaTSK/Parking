package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.BillingException;
import org.hofftech.parking.model.Order;
import org.hofftech.parking.model.enums.OrderOperationType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для управления заказами.
 * Предоставляет методы для добавления заказов и генерации отчетов по заказам пользователя
 * в заданном диапазоне дат.
 */
@Slf4j
public class OrderManagerService {
    private final List<Order> orders = new ArrayList<>();

    private static final int LOAD_COST = 80;
    private static final int UNLOAD_COST = 50;

    /**
     * Добавляет новый заказ в список заказов.
     *
     * @param order Заказ, который необходимо добавить.
     */
    public void addOrder(Order order) {
        orders.add(order);
    }

    /**
     * Генерирует отчет о заказах для указанного пользователя в заданном диапазоне дат.
     *
     * @param userId Идентификатор пользователя, для которого генерируется отчет.
     * @param from   Начальная дата диапазона в формате "dd-MM-yyyy".
     * @param to     Конечная дата диапазона в формате "dd-MM-yyyy".
     * @return Строка с форматом отчета, где каждая строка соответствует одному заказу.
     * @throws BillingException Если формат даты некорректен или не найдены заказы для пользователя
     *                          в указанном диапазоне дат.
     */
    public String generateReport(String userId, String from, String to) {
        LocalDate fromDate;
        LocalDate toDate;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        log.info("Отчет для {} с {} по {}", userId, from, to);
        try {
            fromDate = LocalDate.parse(from, formatter);
            toDate = LocalDate.parse(to, formatter);
        } catch (DateTimeParseException e) {
            throw new BillingException("Некорректный формат даты. Используйте формат: dd-MM-yyyy.");
        }

        List<Order> userOrders = getOrdersByUserIdAndDateRange(userId, fromDate, toDate);

        if (userOrders.isEmpty()) {
            throw new BillingException("Не найдено заказов для пользователя " + userId +
                    " в диапазоне " + fromDate + " - " + toDate);
        }

        return userOrders.stream()
                .map(order -> {
                    int totalCost = calculateTotalCost(order);
                    return String.format(
                            "%s; %s; %d машин; %d посылок; %d рублей",
                            order.getDate().format(formatter),
                            order.getOperationType() == OrderOperationType.LOAD ? "Погрузка" : "Разгрузка",
                            order.getTruckCount(),
                            order.getParcels().size(),
                            totalCost
                    );
                })
                .collect(Collectors.joining("\n"));
    }

    /**
     * Вычисляет общую стоимость заказа на основе типа операции и количества сегментов в каждой посылке.
     *
     * <p>
     * Если список посылок пуст или равен {@code null}, возвращает 0.
     * Иначе для каждой посылки подсчитывается количество сегментов (ненулевых символов) в ее форме,
     * умножается на стоимость за сегмент в зависимости от типа операции (загрузка или разгрузка),
     * и результаты суммируются.
     * </p>
     *
     * @param order Заказ, для которого необходимо вычислить стоимость.
     * @return общая стоимость заказа
     */
    public int calculateTotalCost(Order order) {
        if (order.getParcels() == null || order.getParcels().isEmpty()) {
            return 0;
        }

        int costPerSegment = order.getOperationType() == OrderOperationType.LOAD ? LOAD_COST : UNLOAD_COST;

        return order.getParcels().stream()
                .mapToInt(parcel -> countSegments(parcel.getShape()) * costPerSegment)
                .sum();
    }

    /**
     * Подсчитывает количество сегментов в форме посылки.
     * Сегментом считается любой символ, кроме пробела.
     *
     * @param shape список строк, представляющих форму посылки
     * @return количество сегментов в форме посылки
     */
    private int countSegments(List<String> shape) {
        return shape.stream()
                .mapToInt(row -> (int) row.chars().filter(ch -> ch != ' ').count())
                .sum();
    }

    private List<Order> getOrdersByUserIdAndDateRange(String userId, LocalDate dateFrom, LocalDate dateTo) {
        return orders.stream()
                .filter(order -> order.getUserId().equals(userId))
                .filter(order -> !order.getDate().isBefore(dateFrom))
                .filter(order -> !order.getDate().isAfter(dateTo))
                .collect(Collectors.toList());
    }
}
