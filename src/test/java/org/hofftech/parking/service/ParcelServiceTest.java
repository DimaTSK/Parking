package org.hofftech.parking.service;

import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.dto.TruckDto;
import org.hofftech.parking.model.enums.ParcelType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;

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
    public void testCanAddPackage_Success() {
        assertTrue(parcelService.canAddPackage(truckDto, parcelDto, 0, 0));
    }

    @Test
    public void testCanAddPackage_OutsideBounds() {
        assertFalse(parcelService.canAddPackage(truckDto, parcelDto, 5, 5));
    }

    @Test
    public void testCanAddPackage_Overlapping() {
        truckDto.getGrid()[0][0] = '2';
        assertFalse(parcelService.canAddPackage(truckDto, parcelDto, 0, 0));
    }

    @Test
    public void testAddPackage_Success() {
        assertTrue(parcelService.addPackage(truckDto, parcelDto));
        assertEquals(1, truckDto.getParcelDtos().size());
    }


    @Test
    public void testPlacePackage() {
        parcelService.placePackage(truckDto, parcelDto, 0, 0);
        assertEquals('2', truckDto.getGrid()[0][0]);
        assertEquals('2', truckDto.getGrid()[0][1]);
    }
}
