package org.hofftech.parking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.repository.ParcelRepository;

import java.util.ArrayList;
import java.util.List;

import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class ParsingService {
    private final ParcelRepository parcelRepository;

    private static final Pattern PACKAGE_NAME_PATTERN = Pattern.compile("[“”\"]");
    private static final Pattern PACKAGE_DELIMITER_PATTERN = Pattern.compile(",");

    /**
     * Парсит список строк из файла и возвращает список объектов Parcel.
     *
     * @param lines список строк из файла
     * @return список Parcel
     */
    public List<Parcel> parsePackagesFromFile(List<String> lines) {
        List<Parcel> parcels = new ArrayList<>();
        for (String packageName : lines) {
            String trimmedName = PACKAGE_NAME_PATTERN.matcher(packageName.trim()).replaceAll("");
            if (trimmedName.isEmpty()) {
                continue;
            }
            try {
                Parcel pkg = parcelRepository.findPackage(trimmedName);
                parcels.add(pkg);
            } catch (IllegalArgumentException e) {
                log.warn("Посылка '{}' из файла не найдена и будет пропущена.", trimmedName);
            }
        }
        return parcels;
    }

    /**
     * Получает список объектов Parcel из текстового аргумента.
     *
     * @param parcelsText строка с именами посылок, разделенными запятыми
     * @return список Parcel
     */
    public List<Parcel> getPackagesFromArgs(String parcelsText) {
        if (parcelsText == null || parcelsText.isBlank()) {
            log.warn("Текст посылок пустой.");
            throw new IllegalArgumentException("Аргумент с посылками пуст");
        }

        List<Parcel> parcels = new ArrayList<>();
        String[] names = PACKAGE_DELIMITER_PATTERN.split(parcelsText);

        for (String name : names) {
            String trimmedName = PACKAGE_NAME_PATTERN.matcher(name.trim()).replaceAll("");
            if (!trimmedName.isEmpty()) {
                try {
                    Parcel pkg = parcelRepository.findPackage(trimmedName);
                    parcels.add(pkg);
                } catch (IllegalArgumentException e) {
                    log.warn("Посылка '{}' из аргументов не найдена и будет пропущена.", trimmedName);
                }
            }
        }

        return parcels;
    }
}
