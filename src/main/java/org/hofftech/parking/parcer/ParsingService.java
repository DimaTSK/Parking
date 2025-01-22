package org.hofftech.parking.parcer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.ParcelArgumentException;
import org.hofftech.parking.exception.ParcelNotFoundException;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.repository.ParcelRepository;

import java.util.ArrayList;
import java.util.List;


/**
 * Сервис для парсинга информации о посылках.
 * Предоставляет методы для извлечения посылок из файла и аргументов.
 */
@Slf4j
@RequiredArgsConstructor
public class ParsingService {
    private static final String COMMA_REPLACEMENTS = "[“”\"]";
    private static final String PARCELS_SPLITTER = ",";
    private final ParcelRepository parcelRepository;

    /**
     * Парсит список строк из файла и возвращает список соответствующих посылок.
     *
     * @param lines список строк, представляющих имена посылок
     * @return список объектов {@link Parcel}
     * @throws RuntimeException если посылка не найдена в репозитории
     */
    public List<Parcel> parseParcelsFromFile(List<String> lines) {
        List<Parcel> parcels = new ArrayList<>();
        for (String parcelName : lines) {
            String trimmedName = parcelName.trim().replaceAll(COMMA_REPLACEMENTS, "");
            if (trimmedName.isEmpty()) {
                continue;
            }
            parcelRepository.findParcel(trimmedName).ifPresentOrElse(
                    parcels::add,
                    () -> {
                        throw new RuntimeException("Посылка " + trimmedName + " не найдена!");
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
    public List<Parcel> getParcelFromArgs(String parcelsText) {
        if (parcelsText == null || parcelsText.isBlank()) {
            throw new ParcelArgumentException("Аргумент с посылками пуст");
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
                                    "Посылка '" + trimmedName + "' из аргументов не найдена и не может быть обработана"
                            );
                        }
                );
            }
        }
        return parcels;
    }
}
