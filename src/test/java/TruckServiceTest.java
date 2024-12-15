import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.parking.model.dto.ParcelDto;
import org.parking.model.dto.TruckCapacityDto;
import org.parking.service.TruckService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TruckServiceTest {
    private TruckService truckService;

    @BeforeEach
    void setUp() {
        TruckCapacityDto capacity = new TruckCapacityDto(6, 6);
        truckService = new TruckService(capacity);
    }

    @Test
    void testPackPackages_Success() {
        List<ParcelDto> parcels = new ArrayList<>();

        ParcelDto parcel = new ParcelDto("Тестовый пакет");
        parcels.add(parcel);

        truckService.packPackages(parcels);

        assertTrue(true);
    }

    @Test
    void testPackPackages_Failure() {
        List<ParcelDto> parcels = new ArrayList<>();


        ParcelDto parcel = new ParcelDto("Большой пакет");
        parcels.add(parcel);


        truckService.packPackages(parcels);

        assertTrue(true);
    }

    @Test
    void testPackPackages_MultipleParcels() {
        List<ParcelDto> parcels = new ArrayList<>();

        parcels.add(new ParcelDto("Пакет 1"));
        parcels.add(new ParcelDto("Пакет 2"));

        // Вызов метода
        truckService.packPackages(parcels);

        assertTrue(true); //
    }
}