package org.hofftech.parking.service;

import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.entity.Truck;
import org.hofftech.parking.model.enums.ParcelType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParcelServiceTest {

    private ParcelService parcelService;
    private Truck truck;

    @BeforeEach
    void setUp() {
        parcelService = new ParcelService();
        truck = new Truck();
    }

    @Test
    void testCanAddParcelWithinBounds() {
        ParcelDto parcel = createParcel(ParcelType.TWO);
        assertTrue(parcelService.canAddParcel(truck, parcel, 0, 0));
    }

    @Test
    void testCanAddParcelOutOfBounds() {
        ParcelDto parcel = createParcel(ParcelType.FOUR);
        assertFalse(parcelService.canAddParcel(truck, parcel, 4, 0)); // Выход за пределы
    }

    @Test
    void testCanAddParcelOverlapping() {
        ParcelDto parcel1 = createParcel(ParcelType.TWO);
        ParcelDto parcel2 = createParcel(ParcelType.TWO);
        parcelService.placeParcels(truck, parcel1, 0, 0);
        assertFalse(parcelService.canAddParcel(truck, parcel2, 1, 0)); // Пересечение
    }

    @Test
    void testAddParcelsSuccessfully() {
        ParcelDto parcel = createParcel(ParcelType.THREE);
        assertTrue(parcelService.addParcels(truck, parcel));
    }

    private ParcelDto createParcel(ParcelType parcelType) {
        return new ParcelDto(parcelType, parcelType.ordinal(), null);
    }
}