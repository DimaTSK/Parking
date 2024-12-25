package org.hofftech.parking.service;

import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.entity.Truck;
import org.hofftech.parking.model.enums.ParcelType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class ParcelServiceTest {

    @InjectMocks
    private ParcelService parcelService;

    private Truck truck;
    private ParcelDto parcelDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        truck = new Truck();

        parcelDto = new ParcelDto(ParcelType.TWO, 1, null);
    }

    @Test
    public void testCanAddParcel_Success() {
        assertTrue(parcelService.canAddParcel(truck, parcelDto, 0, 0));
    }

    @Test
    public void testCanAddParcel_OutsideBounds() {
        assertFalse(parcelService.canAddParcel(truck, parcelDto, 5, 5));
    }

    @Test
    public void testCanAddParcel_Overlapping() {
        truck.getGrid()[0][0] = '2';
        assertFalse(parcelService.canAddParcel(truck, parcelDto, 0, 0));
    }

    @Test
    public void testAddParcels_Success() {
        assertTrue(parcelService.addParcels(truck, parcelDto));
        assertEquals(1, truck.getParcelDtos().size());
    }


    @Test
    public void testPlaceParcels() {
        parcelService.placeParcels(truck, parcelDto, 0, 0);
        assertEquals('2', truck.getGrid()[0][0]);
        assertEquals('2', truck.getGrid()[0][1]);
    }
}
