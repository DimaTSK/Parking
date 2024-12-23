package org.hofftech.parking.service;

import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.dto.TruckDto;
import org.hofftech.parking.model.enums.ParcelType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class ParcelServiceTest {

    @InjectMocks
    private ParcelService parcelService;

    private TruckDto truckDto;
    private ParcelDto parcelDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        truckDto = new TruckDto();

        parcelDto = new ParcelDto(ParcelType.TWO, 1, null);
    }

    @Test
    public void testCanAddParcel_Success() {
        assertTrue(parcelService.canAddParcel(truckDto, parcelDto, 0, 0));
    }

    @Test
    public void testCanAddParcel_OutsideBounds() {
        assertFalse(parcelService.canAddParcel(truckDto, parcelDto, 5, 5));
    }

    @Test
    public void testCanAddParcel_Overlapping() {
        truckDto.getGrid()[0][0] = '2';
        assertFalse(parcelService.canAddParcel(truckDto, parcelDto, 0, 0));
    }

    @Test
    public void testAddParcels_Success() {
        assertTrue(parcelService.addParcels(truckDto, parcelDto));
        assertEquals(1, truckDto.getParcelDtos().size());
    }


    @Test
    public void testPlaceParcels() {
        parcelService.placeParcels(truckDto, parcelDto, 0, 0);
        assertEquals('2', truckDto.getGrid()[0][0]);
        assertEquals('2', truckDto.getGrid()[0][1]);
    }
}
