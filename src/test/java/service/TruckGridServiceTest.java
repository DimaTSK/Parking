package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.service.TruckGridService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TruckGridServiceTest {
    private TruckGridService truckGridService;

    @BeforeEach
    public void setUp() {
        truckGridService = new TruckGridService(5, 5);
    }

    @Test
    public void testCanPlacePackage() {
        ParcelDto parcel = new ParcelDto(new String[]{"##", "##"});
        boolean canPlace = truckGridService.canPlacePackage(parcel, 0, 0);

        assertTrue(canPlace);
    }

    @Test
    public void testPlacePackage() {
        ParcelDto parcel = new ParcelDto(new String[]{"##", "##"});
        truckGridService.placePackage(parcel, 0, 0);
        char[][] grid = truckGridService.getTruckDto();

        assertEquals('#', grid[0][0]);
        assertEquals('#', grid[0][1]);
        assertEquals('#', grid[1][0]);
        assertEquals('#', grid[1][1]);
    }
}