package org.hofftech.parking.service;

import org.hofftech.parking.exception.BillingException;
import org.hofftech.parking.model.Order;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.enums.OrderOperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class OrderManagerServiceTest {

    private OrderManagerService orderManagerService;

    @BeforeEach
    void setUp() {
        orderManagerService = new OrderManagerService();
    }

    @Test
    @DisplayName("Generate Report with Incorrect Date Format Should Throw BillingException")
    void generateReport_WithIncorrectDateFormat_ShouldThrowBillingException() {
        Order order = createSampleOrder("user1", LocalDate.of(2023, 4, 25), OrderOperationType.LOAD, 1);
        orderManagerService.addOrder(order);


        assertThatThrownBy(() -> orderManagerService.generateReport("user1", "2023-04-25", "25-04-2023"))
                .isInstanceOf(BillingException.class)
                .hasMessageContaining("Некорректный формат даты. Используйте формат: dd-MM-yyyy.");
    }

    @Test
    @DisplayName("Generate Report When No Orders Found Should Throw BillingException")
    void generateReport_WhenNoOrdersFound_ShouldThrowBillingException() {

        Order order = createSampleOrder("user1", LocalDate.of(2023, 4, 25), OrderOperationType.LOAD, 1);
        orderManagerService.addOrder(order);


        assertThatThrownBy(() -> orderManagerService.generateReport("user1", "26-04-2023", "27-04-2023"))
                .isInstanceOf(BillingException.class)
                .hasMessageContaining("Не найдено заказов для пользователя user1");
    }

    @Test
    @DisplayName("Generate Report for User with Multiple Parcels and Calculate Total Cost")
    void generateReport_WithMultipleParcels_ShouldCalculateTotalCostCorrectly() {

        Parcel parcel1 = new Parcel("Parcel1", Arrays.asList("A A", "AAA"), 'A', null);
        Parcel parcel2 = new Parcel("Parcel2", Collections.singletonList("BB"), 'B', null);

        Order order = new Order(
                "user3",
                LocalDate.of(2023, 5, 10),
                OrderOperationType.LOAD,
                2,
                Arrays.asList(parcel1, parcel2)
        );


        int expectedCost = orderManagerService.calculateTotalCost(order);

        orderManagerService.addOrder(order);


        String report = orderManagerService.generateReport("user3", "10-05-2023", "10-05-2023");


        String expectedReport = String.format(
                "10-05-2023; Погрузка; 2 машин; 2 посылок; %d рублей",
                expectedCost
        );

        assertThat(report).isEqualTo(expectedReport);
    }

    @Test
    @DisplayName("Generate Report with Mixed Operation Types")
    void generateReport_WithMixedOperationTypes_ShouldHandleCorrectly() {

        Parcel parcel1 = new Parcel("Parcel1", Arrays.asList("A A", "AAA"), 'A', null);
        Parcel parcel2 = new Parcel("Parcel2", Collections.singletonList("BB"), 'B', null);

        Order loadOrder = new Order(
                "user4",
                LocalDate.of(2023, 6, 15),
                OrderOperationType.LOAD,
                1,
                List.of(parcel1)
        );

        Order unloadOrder = new Order(
                "user4",
                LocalDate.of(2023, 6, 16),
                OrderOperationType.UNLOAD,
                2,
                Arrays.asList(parcel2)
        );

        orderManagerService.addOrder(loadOrder);
        orderManagerService.addOrder(unloadOrder);


        String report = orderManagerService.generateReport("user4", "15-06-2023", "16-06-2023");


        int loadCost = orderManagerService.calculateTotalCost(loadOrder);
        int unloadCost = orderManagerService.calculateTotalCost(unloadOrder);

        String expectedReport = String.join("\n",
                String.format("15-06-2023; Погрузка; 1 машин; 1 посылок; %d рублей", loadCost),
                String.format("16-06-2023; Разгрузка; 2 машин; 1 посылок; %d рублей", unloadCost)
        );

        assertThat(report).isEqualTo(expectedReport);
    }

    @Test
    @DisplayName("Generate Report with No Parcels Should Have Zero Cost")
    void generateReport_WithNoParcels_ShouldHaveZeroCost() {

        Order order = new Order(
                "user5",
                LocalDate.of(2023, 7, 20),
                OrderOperationType.UNLOAD,
                1,
                Collections.emptyList()
        );

        orderManagerService.addOrder(order);


        String report = orderManagerService.generateReport("user5", "20-07-2023", "20-07-2023");


        String expectedReport = "20-07-2023; Разгрузка; 1 машин; 0 посылок; 0 рублей";
        assertThat(report).isEqualTo(expectedReport);
    }


    private Order createSampleOrder(String userId, LocalDate date, OrderOperationType operationType, int truckCount) {
        Parcel parcel = new Parcel("SampleParcel", Collections.singletonList("ABC"), 'A', null);
        return new Order(
                userId,
                date,
                operationType,
                truckCount,
                Collections.singletonList(parcel)
        );
    }
}
