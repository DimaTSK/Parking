import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.parking.model.dto.ParcelDto;
import org.parking.service.ParcelLoaderService;
import org.parking.service.TruckService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParcelLoaderServiceTest {
    private Path testFilePath;

    @BeforeEach
    void setUp() throws IOException {
        testFilePath = Files.createTempFile("test_packages", ".txt");
        List<String> lines = Arrays.asList(
                "Пакет 1",
                "Размер: 2x2",
                "",
                "Пакет 2",
                "Размер: 3x2",
                "",
                "Пакет 3",
                "Размер: 1x1"
        );
        Files.write(testFilePath, lines);
    }

    @AfterEach
    void tearDown() throws IOException {
        try {
            Thread.sleep(100); // Задержка в 100 миллисекунд
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Files.deleteIfExists(testFilePath);
    }

    @Test
    void testReadPackages_emptyFile() throws IOException {
        Files.write(testFilePath, Arrays.asList());
        List<ParcelDto> parcels = ParcelLoaderService.readPackages(testFilePath.toString());
        assertTrue(parcels.isEmpty(), "Список пакетов должен быть пустым для пустого файла");
    }

    @Test
    void testPackPackages_withValidParcels() throws IOException {
        List<ParcelDto> parcels = ParcelLoaderService.readPackages(testFilePath.toString());
        TruckService truckService = ParcelLoaderService.packPackages(parcels);
        assertNotNull(truckService, "TruckService не должен быть null");
    }

    @Test
    void testPackPackages_withNoParcels() throws IOException {
        // Создание пустого файла
        Files.write(testFilePath, Arrays.asList());
        List<ParcelDto> parcels = ParcelLoaderService.readPackages(testFilePath.toString());
        TruckService truckService = ParcelLoaderService.packPackages(parcels);
        assertNotNull(truckService, "TruckService не должен быть null при пустом списке пакетов");
    }
}