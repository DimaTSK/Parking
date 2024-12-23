package org.hofftech.parking.service;

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
    public void testIsValidPackages_AllValid() {
        ParcelDto parcel1 = new ParcelDto(ParcelType.ONE, 1, null);
        ParcelDto parcel2 = new ParcelDto(ParcelType.TWO, 2, null);
        List<ParcelDto> parcels = Arrays.asList(parcel1, parcel2);
        assertTrue(parcelValidator.isValidPackages(parcels));
    }

    @Test
    public void testIsValidJsonStructure_MissingTrucksKey() {
        Map<String, Object> jsonData = new HashMap<>();
        assertFalse(parcelValidator.isValidJsonStructure(jsonData));
    }

    @Test
    public void testIsValidJsonStructure_InvalidPackage() {
        Map<String, Object> jsonData = new HashMap<>();
        List<Map<String, Object>> trucks = new ArrayList<>();
        Map<String, Object> truck = new HashMap<>();
        List<Map<String, Object>> packages = new ArrayList<>();
        Map<String, Object> pkg = new HashMap<>();
        packages.add(pkg);
        truck.put("packages", packages);
        trucks.add(truck);
        jsonData.put("trucks", trucks);

        assertFalse(parcelValidator.isValidJsonStructure(jsonData));
    }

    @Test
    public void testIsValidJsonStructure_ValidJson() {
        Map<String, Object> jsonData = new HashMap<>();
        List<Map<String, Object>> trucks = new ArrayList<>();
        Map<String, Object> truck = new HashMap<>();
        List<Map<String, Object>> packages = new ArrayList<>();
        Map<String, Object> pkg = new HashMap<>();
        pkg.put("type", "ONE");
        packages.add(pkg);
        truck.put("packages", packages);
        trucks.add(truck);
        jsonData.put("trucks", trucks);

        assertTrue(parcelValidator.isValidJsonStructure(jsonData));
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
