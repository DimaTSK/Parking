package org.hofftech.parking.service;

import org.hofftech.parking.exception.InvalidJsonStructureException;
import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.enums.ParcelType;
import org.hofftech.parking.utill.ParcelValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ParcelValidatorTest {

    private ParcelValidator parcelValidator;

    @BeforeEach
    public void setUp() {
        parcelValidator = new ParcelValidator();
    }

    @Test
    public void testIsValidFile_EmptyFile() {
        List<String> emptyLines = Collections.emptyList();
        assertFalse(parcelValidator.isValidFile(emptyLines));
    }

    @Test
    public void testIsValidFile_NonEmptyFile() {
        List<String> lines = Arrays.asList("line1", "line2");
        assertTrue(parcelValidator.isValidFile(lines));
    }

    @Test
    public void testIsValidParcels_AllValid() {
        // Предположим, что ParcelType.ONE и ParcelType.TWO действительны
        ParcelDto parcel1 = new ParcelDto(ParcelType.ONE, 1, null);
        ParcelDto parcel2 = new ParcelDto(ParcelType.TWO, 2, null);
        List<ParcelDto> parcels = Arrays.asList(parcel1, parcel2);
        assertTrue(parcelValidator.isValidParcels(parcels));
    }

    @Test
    public void testIsValidJsonStructure_InvalidParcels() {
        Map<String, Object> jsonData = new HashMap<>();
        List<Map<String, Object>> trucks = new ArrayList<>();
        Map<String, Object> truck = new HashMap<>();
        List<Map<String, Object>> parcels = new ArrayList<>();
        Map<String, Object> pkg = new HashMap<>();
        parcels.add(pkg); // Пакет без типа
        truck.put("parcels", parcels);
        trucks.add(truck);
        jsonData.put("trucks", trucks);

        assertThrows(InvalidJsonStructureException.class, () -> {
            parcelValidator.validateJsonStructure(jsonData);
        });
    }

    @Test
    public void testIsValidJsonStructure_ValidJson() {
        Map<String, Object> jsonData = new HashMap<>();
        List<Map<String, Object>> trucks = new ArrayList<>();
        Map<String, Object> truck = new HashMap<>();
        List<Map<String, Object>> parcels = new ArrayList<>();
        Map<String, Object> pkg = new HashMap<>();
        pkg.put("type", "ONE"); // Указан тип
        parcels.add(pkg);
        truck.put("parcels", parcels);
        trucks.add(truck);
        jsonData.put("trucks", trucks);

        // Метод не выбрасывает Exception, значит, структура верна
        assertDoesNotThrow(() -> {
            parcelValidator.validateJsonStructure(jsonData);
        });
    }

    @Test
    public void testIsFileExists_FileExists() {
        File file = new File("existingFile.txt");
        try {
            if (file.createNewFile()) {
                assertTrue(parcelValidator.isFileExists(file));
            }
        } catch (Exception e) {
            fail("Не удалось создать тестовый файл.");
        } finally {
            file.delete();
        }
    }

    @Test
    public void testIsFileExists_FileDoesNotExist() {
        File file = new File("nonExistentFile.txt");
        assertFalse(parcelValidator.isFileExists(file));
    }
}
