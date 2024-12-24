package org.hofftech.parking.service;

import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.entity.TruckEntity;
import org.hofftech.parking.model.enums.ParcelType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class ParcelServiceTest {

    @InjectMocks
    private ParcelService parcelService;

    private TruckEntity truckEntity;
    private ParcelDto parcelDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        truckEntity = new TruckEntity();

        parcelDto = new ParcelDto(ParcelType.TWO, 1, null);
    }

    @Test
    public void testCanAddParcel_Success() {
        assertTrue(parcelService.canAddParcel(truckEntity, parcelDto, 0, 0));
    }

    @Test
    public void testCanAddParcel_OutsideBounds() {
        assertFalse(parcelService.canAddParcel(truckEntity, parcelDto, 5, 5));
    }

    @Test
    public void testCanAddParcel_Overlapping() {
        truckEntity.getGrid()[0][0] = '2';
        assertFalse(parcelService.canAddParcel(truckEntity, parcelDto, 0, 0));
    }

    @Test
    public void testAddParcels_Success() {
        assertTrue(parcelService.addParcels(truckEntity, parcelDto));
        assertEquals(1, truckEntity.getParcelDtos().size());
    }


    @Test
    public void testPlaceParcels() {
        parcelService.placeParcels(truckEntity, parcelDto, 0, 0);
        assertEquals('2', truckEntity.getGrid()[0][0]);
        assertEquals('2', truckEntity.getGrid()[0][1]);
    }
}
