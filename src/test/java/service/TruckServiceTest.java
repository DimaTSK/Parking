package service;

import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.dto.TruckCapacityDto;
import org.hofftech.parking.model.dto.TruckDto;
import org.hofftech.parking.service.TruckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TruckServiceTest {

    private TruckService truckService;
    private TruckCapacityDto truckCapacity;

    @BeforeEach
    public void setUp() {
        truckCapacity = new TruckCapacityDto(5, 5); // Ширина 5, Высота 5
        truckService = new TruckService(truckCapacity);
    }

    @Test
    public void testCanPlacePackage() {
        TruckDto truckDto = new TruckDto(truckCapacity.width(), truckCapacity.height());
        String[] parcelLines = {
                "XX",
                "XX"
        };
        ParcelDto parcel = new ParcelDto(parcelLines);

        assertTrue(truckService.canPlacePackage(parcel, 0, 0, truckDto));

        truckService.placePackage(parcel, 0, 0, truckDto);

        assertFalse(truckService.canPlacePackage(parcel, 0, 0, truckDto));

        assertFalse(truckService.canPlacePackage(parcel, 1, 1, truckDto));

        assertTrue(truckService.canPlacePackage(parcel, 3, 3, truckDto));
    }

    @Test
    public void testPlacePackage() {
        TruckDto truckDto = new TruckDto(truckCapacity.width(), truckCapacity.height());
        String[] parcelLines = {
                "XX",
                "XX"
        };
        ParcelDto parcel = new ParcelDto(parcelLines);

        truckService.placePackage(parcel, 0, 0, truckDto);

        char[][] expectedGrid = {
                {'X', 'X', ' ', ' ', ' '},
                {'X', 'X', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' '}
        };

        assertArrayEquals(expectedGrid, truckDto.getGrid());
    }

    @Test
    public void testCannotPlacePackageExceedingBounds() {
        TruckDto truckDto = new TruckDto(truckCapacity.width(), truckCapacity.height());
        String[] parcelLines = {
                "XXX",
                "XXX",
                "XXX"
        };
        ParcelDto parcel = new ParcelDto(parcelLines);
        assertFalse(truckService.canPlacePackage(parcel, 3, 3, truckDto));
    }
}