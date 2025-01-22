package org.hofftech.parking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hofftech.parking.model.enums.OrderOperationType;

import java.time.LocalDate;
import java.util.List;

/**
 * Модель заказа.
 */
@Data
@AllArgsConstructor
public class Order {

    private static final int LOAD_COST = 80;
    private static final int UNLOAD_COST = 50;
    private String userId;
    private LocalDate date;
    private OrderOperationType operationType;
    private int truckCount;
    private List<Parcel> parcels;

    public int getTotalCost() {
        if (parcels == null || parcels.isEmpty()) {
            return 0;
        }

        int costPerSegment = operationType == OrderOperationType.LOAD ? LOAD_COST : UNLOAD_COST;

        return parcels.stream()
                .mapToInt(parcel -> countSegments(parcel.getShape()) * costPerSegment)
                .sum();
    }

    private int countSegments(List<String> shape) {
        return shape.stream()
                .mapToInt(row -> (int) row.chars().filter(ch -> ch != ' ').count())
                .sum();
    }
}