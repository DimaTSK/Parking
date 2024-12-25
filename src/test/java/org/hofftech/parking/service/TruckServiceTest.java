package org.hofftech.parking.service;

import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.entity.Truck;
import org.hofftech.parking.model.enums.ParcelType;
import org.hofftech.parking.factory.TruckFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TruckServiceTest {

    @Mock
    private ParcelDistributor parcelDistributor;

    @Mock
    private TruckFactory truckFactory;

    @Mock
    private ParcelService parcelService;

    @InjectMocks
    private TruckService truckService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddParcelsToMultipleTrucksWithEvenDistribution() {
        List<ParcelDto> parcels = createSampleParcels(10);
        List<Truck> trucks = createSampleTrucks(3);

        when(truckFactory.createTrucks(3)).thenReturn(trucks);

        List<Truck> result = truckService.addParcelsToMultipleTrucks(parcels, 3, true);

        verify(parcelDistributor).distributeParcelsEvenly(parcels, trucks);
        assertEquals(3, result.size());
    }

    @Test
    void testAddParcelsToMultipleTrucksWithoutEvenDistribution() {
        List<ParcelDto> parcels = createSampleParcels(10);
        List<Truck> trucks = createSampleTrucks(1);

        when(truckFactory.createTrucks(1)).thenReturn(trucks);

        List<Truck> result = truckService.addParcelsToMultipleTrucks(parcels, 3, false);

        verify(parcelDistributor).placeParcels(parcels, trucks, 3);
        assertEquals(1, result.size());
    }

    @Test
    void testAddParcelsToIndividualTrucks() {
        List<ParcelDto> parcels = createSampleParcels(5);
        List<Truck> trucks = new ArrayList<>();

        when(parcelService.addParcels(any(Truck.class), any(ParcelDto.class))).thenReturn(true);

        List<Truck> result = truckService.addParcelsToIndividualTrucks(parcels);

        assertEquals(5, result.size());
        for (ParcelDto parcel : parcels) {
            verify(parcelService).addParcels(any(Truck.class), eq(parcel));
        }
    }

    private List<ParcelDto> createSampleParcels(int count) {
        List<ParcelDto> parcels = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            parcels.add(new ParcelDto(ParcelType.ONE, i, null));
        }
        return parcels;
    }

    private List<Truck> createSampleTrucks(int count) {
        List<Truck> trucks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            trucks.add(new Truck());
        }
        return trucks;
    }
}