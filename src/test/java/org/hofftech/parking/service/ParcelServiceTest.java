package org.hofftech.parking.service;

import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParcelStartPosition;
import org.hofftech.parking.model.Truck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class ParcelServiceTest {

    private ParcelService parcelService;
    private Truck truck;

    @BeforeEach
    void setUp() {
        parcelService = new ParcelService();
        truck = new Truck(10, 10);
    }

    @Test
    @DisplayName("Тестирует успешное добавление посылки в пустой грузовик")
    void testAddPackage_SuccessfulAddition() {
        // Определяем форму посылки: квадрат 3x3
        List<String> shape = List.of(
                "XXX",
                "X X",
                "XXX"
        );
        Parcel parcel = new Parcel("Parcel1", shape, 'X', null);

        boolean result = parcelService.tryPack(truck, parcel);
        assertThat(result)
                .as("Посылка должна быть успешно добавлена")
                .isTrue();

        ParcelStartPosition pos = parcel.getParcelStartPosition();
        assertThat(pos)
                .as("Позиция посылки должна быть установлена")
                .isNotNull();

        List<String> expectedShape = parcel.getReversedShape();

        // Извлекаем фактический раздел сетки, где должна быть размещена посылка
        List<String> actualGridSection = getGridSection(truck, pos, expectedShape.size(), expectedShape.get(0).length());

        assertThat(actualGridSection)
                .as("Сетка грузовика должна содержать форму посылки")
                .containsExactlyElementsOf(expectedShape);

        assertThat(truck.getParcels())
                .as("Список посылок грузовика должен содержать добавленную посылку")
                .contains(parcel);
    }

    @Test
    @DisplayName("Тестирует успешное добавление нескольких посылок без пересечений")
    void testAddMultiplePackages_SuccessfulAdditions() {
        // Первая посылка: 3x3 квадрат
        List<String> shape1 = List.of(
                "XXX",
                "X X",
                "XXX"
        );
        Parcel parcel1 = new Parcel("Parcel1", shape1, 'X', null);

        // Вторая посылка: 2x2 квадрат
        List<String> shape2 = List.of(
                "XX",
                "XX"
        );
        Parcel parcel2 = new Parcel("Parcel2", shape2, 'Y', null);

        boolean result1 = parcelService.tryPack(truck, parcel1);
        boolean result2 = parcelService.tryPack(truck, parcel2);

        assertThat(result1)
                .as("Первая посылка должна быть успешно добавлена")
                .isTrue();

        assertThat(result2)
                .as("Вторая посылка должна быть успешно добавлена")
                .isTrue();

        assertThat(truck.getParcels())
                .as("Список посылок должен содержать первую посылку")
                .contains(parcel1)
                .as("Список посылок должен содержать вторую посылку")
                .contains(parcel2);
    }

    @Test
    @DisplayName("Тестирует добавление посылки, которая выходит за пределы грузовика")
    void testAddPackage_ExceedsTruckLimits() {
        List<String> shape = List.of("XXXXXXXXXXX"); // Ширина 11
        Parcel parcel = new Parcel("BigParcel", shape, 'B', null);

        boolean result = parcelService.tryPack(truck, parcel);
        assertThat(result)
                .as("Посылка не должна быть добавлена из-за превышения ширины грузовика")
                .isFalse();

        assertThat(truck.getParcels())
                .as("Список посылок грузовика не должен содержать превышающую посылку")
                .doesNotContain(parcel);
    }

    @Test
    @DisplayName("Тестирует добавление посылки в верхний левый угол грузовика")
    void testAddPackage_PlaceAtTopLeftCorner() {
        List<String> shape = List.of(
                "XX",
                "XX"
        );
        Parcel parcel = new Parcel("Parcel1", shape, 'X', null);

        boolean result = parcelService.tryPack(truck, parcel);
        assertThat(result)
                .as("Посылка должна быть успешно добавлена")
                .isTrue();

        ParcelStartPosition pos = parcel.getParcelStartPosition();
        assertThat(pos.x())
                .as("X позиция должна быть 0")
                .isEqualTo(0);
        assertThat(pos.y())
                .as("Y позиция должна быть 0")
                .isEqualTo(0);

        List<String> expectedShape = parcel.getReversedShape();

        // Извлекаем фактический раздел сетки, где должна быть размещена посылка
        List<String> actualGridSection = getGridSection(truck, pos, expectedShape.size(), expectedShape.get(0).length());

        assertThat(actualGridSection)
                .as("Сетка грузовика должна содержать форму посылки")
                .containsExactlyElementsOf(expectedShape);
    }

    @Test
    @DisplayName("Тестирует добавление посылки, когда грузовик полностью заполнен")
    void testAddPackage_FullTruck() {
        List<String> smallShape = List.of(
                "XX",
                "XX"
        );

        // Используем вспомогательный метод для заполнения грузовика посылками
        fillTruckWithParcels(smallShape, 'S', (truck.getWidth() / 2) * (truck.getHeight() / 2));

        Parcel extraParcel = new Parcel("ExtraParcel", smallShape, 'E', null);
        boolean extraResult = parcelService.tryPack(truck, extraParcel);
        assertThat(extraResult)
                .as("Дополнительная посылка не должна быть добавлена в полностью заполненный грузовик")
                .isFalse();

        assertThat(truck.getParcels())
                .as("Список посылок грузовика не должен содержать дополнительную пересекающуюся посылку")
                .doesNotContain(extraParcel);
    }


    /**
     * Вспомогательный метод для извлечения секции сетки грузовика, соответствующей положению посылки.
     *
     * @param truck     Грузовик с сеткой.
     * @param pos       Начальная позиция посылки.
     * @param height    Высота формы посылки.
     * @param width     Ширина формы посылки.
     * @return Список строк, представляющих соответствующую секцию сетки грузовика.
     */
    private List<String> getGridSection(Truck truck, ParcelStartPosition pos, int height, int width) {
        char[][] grid = truck.getGrid();
        List<String> section = new ArrayList<>();
        for (int row = 0; row < height; row++) {
            StringBuilder sb = new StringBuilder();
            for (int col = 0; col < width; col++) {
                sb.append(grid[pos.y() + row][pos.x() + col]);
            }
            section.add(sb.toString());
        }
        return section;
    }

    /**
     * Вспомогательный метод для заполнения грузовика посылками.
     *
     * @param shape  Форма посылки.
     * @param marker Маркер посылки.
     * @param count  Количество посылок для добавления.
     */
    private void fillTruckWithParcels(List<String> shape, char marker, int count) {
        for (int i = 0; i < count; i++) {
            Parcel parcel = new Parcel("Parcel" + i, shape, marker, null);
            boolean result = parcelService.tryPack(truck, parcel);
            assertThat(result)
                    .as("Посылка должна быть успешно добавлена")
                    .isTrue();
        }
    }
}