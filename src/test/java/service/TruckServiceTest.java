package service;

import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.dto.TruckCapacityDto;
import org.hofftech.parking.service.TruckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TruckServiceTest {

    private TruckService truckService;

    @BeforeEach
    public void setUp() {
        TruckCapacityDto truckCapacity = new TruckCapacityDto(5, 5); // Ширина 5, Высота 5
        truckService = new TruckService(truckCapacity, truckCapacity.width(), truckCapacity.height());
    }

    @Test
    public void testCanPlacePackage() {
        String[] parcelLines = {
                "XX",
                "XX"
        };
        ParcelDto parcel = new ParcelDto(parcelLines);

        assertTrue(truckService.canPlacePackage(parcel, 0, 0));

        truckService.placePackage(parcel, 0, 0);

        assertFalse(truckService.canPlacePackage(parcel, 0, 0));

        assertFalse(truckService.canPlacePackage(parcel, 1, 1));

        assertTrue(truckService.canPlacePackage(parcel, 3, 3));
    }

    @Test
    public void testPlacePackage() {
        String[] parcelLines = {
                "XX",
                "XX"
        };
        ParcelDto parcel = new ParcelDto(parcelLines);

        truckService.placePackage(parcel, 0, 0);

        char[][] expectedGrid = {
                {'X', 'X', ' ', ' ', ' '},
                {'X', 'X', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' '}
        };

        assertArrayEquals(expectedGrid, truckService.getTruckDto());
    }
    @Test
    public void testCannotPlacePackageExceedingBounds() {
        String[] parcelLines = {
                "XXX",
                "XXX",
                "XXX"
        };
        ParcelDto parcel = new ParcelDto(parcelLines);
        assertFalse(truckService.canPlacePackage(parcel, 3, 3));
    }
}