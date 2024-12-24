package org.hofftech.parking.service;

import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.entity.TruckEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TruckServiceTest {

    @Mock
    private ParcelService parcelService;

    @InjectMocks
    private TruckService truckService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddParcelsToMultipleTrucks_ExceedMaxTrucks() {
        List<ParcelDto> parcels = createParcelList(20);
        when(parcelService.addParcels(any(TruckEntity.class), any(ParcelDto.class))).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            truckService.addParcelsToMultipleTrucks(parcels, 1, false);
        });
    }

    @Test
    public void testDistributeParcelsEvenly_NoTrucks() {
        List<ParcelDto> parcels = createParcelList(10);

        assertThrows(IllegalArgumentException.class, () -> {
            truckService.distributeParcelsEvenly(parcels, new ArrayList<>());
        });
    }

    @Test
    public void testDistributeParcelsEvenly_Success() {
        List<ParcelDto> parcels = createParcelList(10);
        List<TruckEntity> trucks = createTruckList(2);
        when(parcelService.addParcels(any(TruckEntity.class), any(ParcelDto.class))).thenReturn(true);

        truckService.distributeParcelsEvenly(parcels, trucks);

        verify(parcelService, atLeastOnce()).addParcels(any(TruckEntity.class), any(ParcelDto.class));
    }

    private List<ParcelDto> createParcelList(int count) {
        List<ParcelDto> parcels = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ParcelDto parcel = mock(ParcelDto.class);
            when(parcel.getId()).thenReturn(i);
            parcels.add(parcel);
        }
        return parcels;}

    private List<TruckEntity> createTruckList(int count) {
        List<TruckEntity> trucks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TruckEntity truck = mock(TruckEntity.class);
            trucks.add(truck);
        }
        return trucks;}
}
