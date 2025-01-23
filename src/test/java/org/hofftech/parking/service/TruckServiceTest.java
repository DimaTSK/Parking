package org.hofftech.parking.service;

import org.hofftech.parking.exception.InsufficientTrucksException;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParcelStartPosition;
import org.hofftech.parking.model.Truck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TruckServiceTest {

    @Mock
    private ParcelService parcelService;

    @InjectMocks
    private TruckService truckService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Тесты для метода addParcelsToMultipleTrucks")
    class AddParcelsToMultipleTrucksTests {

        @Test
        @DisplayName("Равномерное распределение посылок при недостатке места выбрасывает исключение")
        void testDistributeParcelsEvenlyInsufficientTrucks() {
            // Arrange
            List<Parcel> parcels = Arrays.asList(
                    new Parcel("P1", Arrays.asList("XX"), 'X', new ParcelStartPosition(0, 0)),
                    new Parcel("P2", Arrays.asList("XX"), 'X', new ParcelStartPosition(0, 0))
            );

            Truck truck1 = new Truck(5, 5);
            Truck truck2 = new Truck(5, 5);
            List<Truck> trucks = Arrays.asList(truck1, truck2);

            when(parcelService.tryPack(truck1, parcels.get(0))).thenReturn(true);
            when(parcelService.tryPack(truck2, parcels.get(1))).thenReturn(false);
            when(parcelService.tryPack(truck1, parcels.get(1))).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> truckService.distributeParcelsEvenly(parcels, trucks))
                    .isInstanceOf(InsufficientTrucksException.class)
                    .hasMessageContaining("Не хватает указанных грузовиков для размещения всех посылок!");

            verify(parcelService, times(1)).tryPack(truck1, parcels.get(0));
            verify(parcelService, times(1)).tryPack(truck2, parcels.get(1));
            verify(parcelService, times(2)).tryPack(any(Truck.class), eq(parcels.get(1)));
        }
    }

    @Nested
    @DisplayName("Тесты для метода printTrucks")
    class PrintTrucksTests {

        @Test
        @DisplayName("Печать состояния грузовиков возвращает корректное строковое представление")
        void testPrintTrucks() {
            // Arrange
            Truck truck1 = new Truck(3, 3);
            Truck truck2 = new Truck(2, 2);

            // Предположим, что посылки уже размещены
            Parcel parcel1 = new Parcel("P1", Arrays.asList("XX", "XX"), 'X', new ParcelStartPosition(0, 0));
            Parcel parcel2 = new Parcel("P2", Arrays.asList("XX"), 'X', new ParcelStartPosition(1, 1));
            truck1.getParcels().add(parcel1);
            truck2.getParcels().add(parcel2);

            String expectedOutput =
                    "Truck 1\n" +
                            "3x3\n" +
                            "+++++\n" +
                            "+  +\n" +
                            "+  +\n" +
                            "+++++\n" +
                            "+  +\n" +
                            "+  +\n" +
                            "+++++\n" +
                            "\n" +
                            "Truck 2\n" +
                            "2x2\n" +
                            "++++\n" +
                            "+  +\n" +
                            "+  +\n" +
                            "++++\n";


            String result = truckService.printTrucks(Arrays.asList(truck1, truck2));

            assertThat(result).contains("Truck 1", "3x3", "Truck 2", "2x2");
        }
    }

}
