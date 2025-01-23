package org.hofftech.parking.service;

import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.Truck;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormatterService {

    private static final String MARKDOWN_CODE_BLOCK_START = "```\n";
    private static final String MARKDOWN_CODE_BLOCK_END = "\n```";

    /**
     * Форматирует переданную строку как Markdown-блок кода.
     *
     * @param response строка для форматирования
     * @return отформатированный Markdown-блок кода
     */
    public String formatAsMarkdownCodeBlock(String response) {
        return MARKDOWN_CODE_BLOCK_START + response + MARKDOWN_CODE_BLOCK_END;
    }

    /**
     * Форматирует список посылок в читаемую строку.
     *
     * @param parcels список посылок
     * @return отформатированный список посылок или сообщение, если список пуст
     */
    public String formatParcelList(List<Parcel> parcels) {
        if (parcels.isEmpty()) {
            return "Нет доступных посылок.";
        } else {
            StringBuilder output = new StringBuilder("Список всех посылок:\n");
            parcels.forEach(parcel -> output.append(parcel).append("\n"));
            return output.toString();
        }
    }

    /**
     * Форматирует список посылок и оборачивает его в Markdown-блок кода.
     *
     * @param parcels список посылок
     * @return отформатированная Markdown-строка
     */
    public String formatParcelListAsMarkdown(List<Parcel> parcels) {
        String formattedList = formatParcelList(parcels);
        return formatAsMarkdownCodeBlock(formattedList);
    }

    /**
     * Возвращает строковое представление загруженности конкретного грузовика.
     *
     * <p>
     * Отображает содержимое грузовика в виде сетки с границами.
     * Пустые ячейки обозначаются пробелами.
     * </p>
     *
     * @param truck грузовик для отображения
     * @return строковое представление грузовика
     */
    public String getTruckRepresentation(Truck truck) {
        StringBuilder truckRepresentation = new StringBuilder();
        truckRepresentation.append("+").append("+".repeat(truck.getWidth())).append("+\n");

        for (int y = truck.getHeight() - 1; y >= 0; y--) {
            truckRepresentation.append("+");
            for (int x = 0; x < truck.getWidth(); x++) {
                char cell = truck.getGrid()[y][x];
                truckRepresentation.append(cell == '\0' ? ' ' : cell);
            }
            truckRepresentation.append("+\n");
        }
        truckRepresentation.append("+").append("+".repeat(truck.getWidth())).append("+\n");

        return truckRepresentation.toString();
    }
}
