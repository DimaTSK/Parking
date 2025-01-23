package org.hofftech.parking.parcer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.ParcelArgumentException;
import org.hofftech.parking.exception.ParcelNotFoundException;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.repository.ParcelRepository;
import org.hofftech.parking.validator.ParcelValidator;
import org.hofftech.parking.service.FileReaderService;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class ParsingService {

    private static final Pattern COMMA_REPLACEMENT_PATTERN = Pattern.compile("[“”\"]");
    private static final String PARCELS_SPLITTER = ",";
    private final ParcelRepository parcelRepository;
    private final ParcelValidator parcelValidator;

    /**
     * Получает список посылок из файла или из текстового представления.
     *
     * <p>
     * В зависимости от предоставленных аргументов, метод читает посылки из файла или
     * парсит их из текстовой строки. После этого выполняется валидация полученных данных.
     * </p>
     *
     * @param parcelsFile путь к файлу с посылками
     * @param parcelsText текстовое представление посылок
     * @return список объектов {@link Parcel}, представляющих посылки
     * @throws IllegalArgumentException если посылки не представлены
     */
    public List<Parcel> getParcels(Path parcelsFile, String parcelsText) {
        List<Parcel> parcels = new ArrayList<>();
        if (parcelsFile != null) {
            List<String> lines = FileReaderService.readAllLines(parcelsFile);
            parcelValidator.validateFile(lines);
            parcels = parseFileLines(parcelsFile, lines);
        } else if (parcelsText != null && !parcelsText.isEmpty()) {
            parcels = parceParcelFromArgs(parcelsText);
        }
        if (parcels.isEmpty()) {
            throw new IllegalArgumentException("Упаковки не представлены, продолжение работы невозможно");
        }
        return parcels;
    }

    /**
     * Парсит список строк из файла и возвращает список соответствующих посылок.
     *
     * @param filePath путь к файлу с посылками
     * @param lines    список строк из файла
     * @return список объектов {@link Parcel}, представляющих посылки
     * @throws ParcelNotFoundException если ни одна посылка не была распарсена
     */
    protected List<Parcel> parseFileLines(Path filePath, List<String> lines) {
        List<Parcel> parcels = parseParcelsFromFile(lines);
        if (parcels.isEmpty()) {
            throw new ParcelNotFoundException("Не удалось распарсить ни одной упаковки из файла: " + filePath);
        }
        return parcels;
    }

    /**
     * Парсит список строк из файла и возвращает список соответствующих посылок.
     *
     * @param lines список строк, представляющих имена посылок
     * @return список объектов {@link Parcel}
     * @throws ParcelNotFoundException если посылка не найдена в репозитории
     */
    public List<Parcel> parseParcelsFromFile(List<String> lines) {
        List<Parcel> parcels = new ArrayList<>();
        for (String parcelName : lines) {
            String trimmedName = COMMA_REPLACEMENT_PATTERN.matcher(parcelName.trim()).replaceAll("");
            if (trimmedName.isEmpty()) {
                continue;
            }
            parcelRepository.findParcel(trimmedName).ifPresentOrElse(
                    parcels::add,
                    () -> {
                        throw new ParcelNotFoundException("Посылка \"" + trimmedName + "\" не найдена!");
                    }
            );
        }
        return parcels;
    }

    /**
     * Извлекает посылки из строки аргументов и возвращает список соответствующих посылок.
     *
     * @param parcelsText строка, содержащая имена посылок, разделенные запятыми
     * @return список объектов {@link Parcel}
     * @throws ParcelArgumentException если строка аргументов пуста или null
     * @throws ParcelNotFoundException если одна из посылок не найдена в репозитории
     */
    public List<Parcel> parceParcelFromArgs(String parcelsText) {
        if (parcelsText == null || parcelsText.isBlank()) {
            throw new ParcelArgumentException("Аргумент с посылками пуст.");
        }
        List<Parcel> parcels = new ArrayList<>();
        String[] names = parcelsText.split(PARCELS_SPLITTER);
        for (String name : names) {
            String trimmedName = name.trim();
            if (!trimmedName.isEmpty()) {
                parcelRepository.findParcel(trimmedName).ifPresentOrElse(
                        parcels::add,
                        () -> {
                            throw new ParcelNotFoundException(
                                    "Посылка \"" + trimmedName + "\" из аргументов не найдена и не может быть обработана."
                            );
                        }
                );
            }
        }
        return parcels;
    }
}
