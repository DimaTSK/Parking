package org.hofftech.parking.service;

import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.dto.TruckDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
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
    public void testAddPackagesToMultipleTrucks_ExceedMaxTrucks() {
        List<ParcelDto> parcels = createParcelList(20);
        when(parcelService.addPackage(any(TruckDto.class), any(ParcelDto.class))).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            truckService.addPackagesToMultipleTrucks(parcels, 1, false);
        });
    }

    @Test
    public void testDistributePackagesEvenly_NoTrucks() {
        List<ParcelDto> parcels = createParcelList(10);

        assertThrows(IllegalArgumentException.class, () -> {
            truckService.distributePackagesEvenly(parcels, new ArrayList<>());
        });
    }

    @Test
    public void testDistributePackagesEvenly_Success() {
        List<ParcelDto> parcels = createParcelList(10);
        List<TruckDto> trucks = createTruckList(2);
        when(parcelService.addPackage(any(TruckDto.class), any(ParcelDto.class))).thenReturn(true);

        truckService.distributePackagesEvenly(parcels, trucks);

        verify(parcelService, atLeastOnce()).addPackage(any(TruckDto.class), any(ParcelDto.class));
    }

    private List<ParcelDto> createParcelList(int count) {
        List<ParcelDto> parcels = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ParcelDto parcel = mock(ParcelDto.class);
            when(parcel.getId()).thenReturn(i);
            parcels.add(parcel);
        }
        return parcels;
    }

    private List<TruckDto> createTruckList(int count) {
        List<TruckDto> trucks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            trucks.add(new TruckDto());
        }
        return trucks;
    }
}
