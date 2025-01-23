package org.hofftech.parking.service;

import org.hofftech.parking.exception.InsufficientTrucksException;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParcelStartPosition;
import org.hofftech.parking.model.Truck;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TruckServiceTest {

    @Mock
    private ParcelService parcelService;

    @InjectMocks
    private TruckService truckService;

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


}
