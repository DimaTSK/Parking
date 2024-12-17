package dto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.dto.TruckDto;

import static org.junit.jupiter.api.Assertions.*;

public class TruckDtoTest {
    private TruckDto truckDto;

    @BeforeEach
    public void setUp() {
        truckDto = new TruckDto(5, 5);
    }

    @Test
    public void testCanPlaceParcel() {
        ParcelDto parcel = new ParcelDto(new String[]{"##", "##"});
        boolean canPlace = truckDto.canPlaceParcel(parcel, 0, 0);

        assertTrue(canPlace);
    }

    @Test
    public void testPlaceParcel() {
        ParcelDto parcel = new ParcelDto(new String[]{"##", "##"});
        truckDto.placeParcel(parcel, 0, 0);

        char[][] grid = truckDto.getGrid();
        assertEquals('#', grid[0][0]);
        assertEquals('#', grid[0][1]);
        assertEquals('#', grid[1][0]);
        assertEquals('#', grid[1][1]);
    }
}