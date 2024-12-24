package org.hofftech.parking.service;
import org.hofftech.parking.model.entity.TruckEntity;
import org.hofftech.parking.utill.ParcelParser;
import org.hofftech.parking.utill.ParcelValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.hofftech.parking.utill.FileReader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FileProcessingServiceTest {

    @Mock
    private FileReader fileReader;

    @Mock
    private ParcelParser parcelParser;

    @Mock
    private ParcelValidator parcelValidator;

    @Mock
    private TruckService truckService;

    @Mock
    private JsonProcessingService jsonProcessingService;

    @InjectMocks
    private FileProcessingService fileProcessingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessFile_InvalidFile() throws IOException {
        Path filePath = Path.of("invalidFile.txt");

        when(fileReader.readAllLines(filePath)).thenReturn(List.of("line1", "line2"));
        when(parcelValidator.isValidFile(any())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> fileProcessingService.processFile(filePath, true, false, 5, false));

        verify(fileReader).readAllLines(filePath);
        verify(parcelValidator).isValidFile(any());
        verifyNoMoreInteractions(parcelParser, truckService);
    }

    @Test
    void testReadFile_Exception() throws IOException {
        Path filePath = Path.of("exceptionFile.txt");

        when(fileReader.readAllLines(filePath)).thenThrow(new RuntimeException("Ошибка чтения"));

        assertThrows(RuntimeException.class, () -> fileProcessingService.processFile(filePath, true, false, 5, false));

        verify(fileReader).readAllLines(filePath);
    }

    @Test
    void testSaveTrucksToFile_Exception() {
        List<TruckEntity> truckEntities = Collections.singletonList(new TruckEntity());

        doThrow(new RuntimeException("Ошибка сохранения")).when(jsonProcessingService).saveToJson(any());

        assertThrows(RuntimeException.class, () -> fileProcessingService.saveTrucksToFile(truckEntities));

        verify(jsonProcessingService).saveToJson(truckEntities);
    }
}
