package org.hofftech.parking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.repository.ParcelRepository;

import java.util.ArrayList;
import java.util.List;

import java.util.regex.Pattern;

/**
 * {@code ParsingService} — сервисный класс, отвечающий за парсинг данных о посылках из различных источников.
 * <p>
 * Класс предоставляет методы для обработки списков строк из файлов и аргументов командной строки,
 * преобразуя их в объекты {@link Parcel}. Для поиска и валидации посылок используется {@link ParcelRepository}.
 * </p>
 *
 * <p><b>Примечание:</b> Класс предполагает, что {@code ParcelRepository} корректно инициализирован и содержит
 * необходимые данные о посылках.</p>
 *
 * @автор [Ваше Имя]
 * @версия 1.0
 * @с момента 2023-04-27
 */
@Slf4j
@RequiredArgsConstructor
public class ParsingService {

    /**
     * Репозиторий для доступа к данным о посылках.
     */
    private final ParcelRepository parcelRepository;

    /**
     * Шаблон для удаления кавычек из имен посылок.
     */
    private static final Pattern PACKAGE_NAME_PATTERN = Pattern.compile("[“”\"]");

    /**
     * Шаблон для разделения имен посылок по запятым.
     */
    private static final Pattern PACKAGE_DELIMITER_PATTERN = Pattern.compile(",");

    /**
     * Парсит список строк из файла и возвращает список объектов {@link Parcel}.
     * <p>
     * Каждая строка рассматривается как имя посылки. Кавычки удаляются из имени, после чего
     * пытается найти соответствующий объект {@code Parcel} в {@link ParcelRepository}.
     * Если посылка не найдена, она пропускается с соответствующим логированием.
     * </p>
     *
     * @param lines список строк из файла
     * @return список {@link Parcel}
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
     * Получает список объектов {@link Parcel} из текстового аргумента.
     * <p>
     * Строка с именами посылок разбивается по запятым, после чего из каждого имени удаляются кавычки.
     * Для каждого получения имени пытается найти соответствующий объект {@code Parcel} в {@link ParcelRepository}.
     * Если посылка не найдена, она пропускается с соответствующим логированием.
     * </p>
     *
     * @param parcelsText строка с именами посылок, разделенными запятыми
     * @return список {@link Parcel}
     * @throws IllegalArgumentException если входная строка {@code null} или пустая
     */
    public List<Parcel> getPackagesFromArgs(String parcelsText) {
        if (parcelsText == null || parcelsText.isBlank()) {
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
