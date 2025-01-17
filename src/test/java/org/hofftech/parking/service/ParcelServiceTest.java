package org.hofftech.parking.service;

import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParcelStartPosition;
import org.hofftech.parking.model.Truck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для {@link ParcelService}.
 * <p>
 * Этот класс проверяет различные сценарии добавления посылок в грузовик,
 * включая успешные добавления и различные случаи неудачных добавлений.
 * </p>
 */
class ParcelServiceTest {

    private ParcelService parcelService;
    private Truck truck;

    @BeforeEach
    void setUp() {
        parcelService = new ParcelService();
        truck = new Truck(10, 10);
    }

    /**
     * Тестирует успешное добавление посылки в пустой грузовик.
     */
    @Test
    void testAddPackage_SuccessfulAddition() {
        // Определяем форму посылки: квадрат 3x3
        List<String> shape = Arrays.asList(
                "XXX",
                "X X",
                "XXX"
        );
        Parcel parcel = new Parcel("Parcel1", shape, 'X', null);

        boolean result = parcelService.addPackage(truck, parcel);
        assertTrue(result, "Посылка должна быть успешно добавлена");

        assertNotNull(parcel.getParcelStartPosition(), "Позиция посылки должна быть установлена");

        ParcelStartPosition pos = parcel.getParcelStartPosition();
        int x = pos.getX();
        int y = pos.getY();


        List<String> uniqueShape = parcel.getUniqueShape();

        for (int row = 0; row < uniqueShape.size(); row++) {
            String rowStr = uniqueShape.get(row);
            for (int col = 0; col < rowStr.length(); col++) {
                char expected = rowStr.charAt(col);
                if (expected != ' ') {
                    assertEquals(expected, truck.getGrid()[y + row][x + col],
                            String.format("Сетка грузовика должна содержать символ '%c' на позиции (%d, %d)", expected, y + row, x + col));
                }
            }
        }


        assertTrue(truck.getParcels().contains(parcel), "Список посылок грузовика должен содержать добавленную посылку");
    }

    /**
     * Тестирует успешное добавление нескольких посылок без пересечений.
     */
    @Test
    void testAddMultiplePackages_SuccessfulAdditions() {
        // Первая посылка: 3x3 квадрат
        List<String> shape1 = Arrays.asList(
                "XXX",
                "X X",
                "XXX"
        );
        Parcel parcel1 = new Parcel("Parcel1", shape1, 'X', null);

        // Вторая посылка: 2x2 квадрат
        List<String> shape2 = Arrays.asList(
                "XX",
                "XX"
        );
        Parcel parcel2 = new Parcel("Parcel2", shape2, 'Y', null);

        boolean result1 = parcelService.addPackage(truck, parcel1);
        boolean result2 = parcelService.addPackage(truck, parcel2);

        assertTrue(result1, "Первая посылка должна быть успешно добавлена");
        assertTrue(result2, "Вторая посылка должна быть успешно добавлена");


        assertTrue(truck.getParcels().contains(parcel1), "Список посылок должен содержать первую посылку");
        assertTrue(truck.getParcels().contains(parcel2), "Список посылок должен содержать вторую посылку");
    }

    /**
     * Тестирует добавление посылки, которая выходит за пределы грузовика.
     */
    @Test
    void testAddPackage_ExceedsTruckLimits() {

        List<String> shape = Arrays.asList(
                "XXXXXXXXXXX" // Ширина 11
        );
        Parcel parcel = new Parcel("BigParcel", shape, 'B', null);

        boolean result = parcelService.addPackage(truck, parcel);
        assertFalse(result, "Посылка не должна быть добавлена из-за превышения ширины грузовика");


        assertFalse(truck.getParcels().contains(parcel), "Список посылок грузовика не должен содержать превышающую посылку");
    }

    @Test
    void testAddPackage_PlaceAtTopLeftCorner() {
        List<String> shape = Arrays.asList(
                "XX",
                "XX"
        );
        Parcel parcel = new Parcel("Parcel1", shape, 'X', null);

        boolean result = parcelService.addPackage(truck, parcel);
        assertTrue(result, "Посылка должна быть успешно добавлена");

        ParcelStartPosition pos = parcel.getParcelStartPosition();
        assertEquals(0, pos.getX(), "X позиция должно быть 0");
        assertEquals(0, pos.getY(), "Y позиция должно быть 0");

        for (int y = 0; y < shape.size(); y++) {
            for (int x = 0; x < shape.get(y).length(); x++) {
                char expected = shape.get(y).charAt(x);
                assertEquals(expected, truck.getGrid()[pos.getY() + y][pos.getX() + x],
                        String.format("Сетка грузовика должна содержать символ '%c' на позиции (%d, %d)", expected, pos.getY() + y, pos.getX() + x));
            }
        }
    }

    /**
     * Тестирует добавление посылки, когда грузовик полностью заполнен.
     */
    @Test
    void testAddPackage_FullTruck() {
        List<String> smallShape = Arrays.asList(
                "XX",
                "XX"
        );

        int numParcels = (truck.getWidth() / 2) * (truck.getHeight() / 2);
        for (int i = 0; i < numParcels; i++) {
            Parcel parcel = new Parcel("Parcel" + i, smallShape, 'S', null);
            boolean result = parcelService.addPackage(truck, parcel);
            assertTrue(result, "Посылка должна быть успешно добавлена");
        }


        Parcel extraParcel = new Parcel("ExtraParcel", smallShape, 'E', null);
        boolean extraResult = parcelService.addPackage(truck, extraParcel);
        assertFalse(extraResult, "Дополнительная посылка не должна быть добавлена в полностью заполненный грузовик");
    }

    /**
     * Проверяет, что методы `canAddPackage` и `placePackage` корректно выполняют свои обязанности.
     * Этот тест использует только публичный метод `addPackage`, чтобы проверить всю логику.
     */
    @Test
    void testAddPackage_CorrectBehavior() {

        List<String> shape1 = Arrays.asList(
                "X",
                "XXX",
                " X "
        );
        Parcel parcel1 = new Parcel("Parcel1", shape1, 'A', null);

        List<String> shape2 = Arrays.asList(
                "XX",
                "XX"
        );
        Parcel parcel2 = new Parcel("Parcel2", shape2, 'B', null);

        List<String> shape3 = Arrays.asList(
                "XXX",
                "X X",
                "XXX"
        );
        Parcel parcel3 = new Parcel("Parcel3", shape3, 'C', null);


        assertTrue(parcelService.addPackage(truck, parcel1), "Parcel1 должна быть успешно добавлена");
        assertTrue(parcelService.addPackage(truck, parcel2), "Parcel2 должна быть успешно добавлена");
        assertTrue(parcelService.addPackage(truck, parcel3), "Parcel3 должна быть успешно добавлена");


        assertEquals(3, truck.getParcels().size(), "В грузовике должно быть 3 посылки");

        for (Parcel parcel : truck.getParcels()) {
            ParcelStartPosition pos = parcel.getParcelStartPosition();
            assertNotNull(pos, "Позиция посылки должна быть установлена");

            List<String> uniqueShape = parcel.getUniqueShape();
            for (int row = 0; row < uniqueShape.size(); row++) {
                for (int col = 0; col < uniqueShape.get(row).length(); col++) {
                    char expected = uniqueShape.get(row).charAt(col);
                    if (expected != ' ') {
                        assertEquals(expected, truck.getGrid()[pos.getY() + row][pos.getX() + col],
                                String.format("Сетка грузовика должна содержать символ '%c' на позиции (%d, %d)", expected, pos.getY() + row, pos.getX() + col));
                    }
                }
            }
        }
    }
}
