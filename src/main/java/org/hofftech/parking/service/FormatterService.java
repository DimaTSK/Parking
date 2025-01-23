package org.hofftech.parking.service;

import org.hofftech.parking.model.Parcel;
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
}
