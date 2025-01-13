package org.hofftech.parking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.repository.ParcelRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ParsingService {
    private final ParcelRepository parcelRepository;

    public List<Parcel> parsePackagesFromFile(List<String> lines) {
        List<Parcel> parcels = new ArrayList<>();
        for (String packageName : lines) {
            String trimmedName = packageName.trim().replaceAll("[“”\"]", "");
            if (trimmedName.isEmpty()) {
                continue;
            }
            try {
                Parcel pkg = parcelRepository.findPackage(trimmedName);
                parcels.add(pkg);
            } catch (IllegalArgumentException e) {
                log.warn("Посылка '{}' из файла  не найдена и будет пропущена.", trimmedName);
            }
        }
        return parcels;
    }

    public List<Parcel> getPackagesFromArgs(String parcelsText) {
        if (parcelsText == null || parcelsText.isBlank()) {
            log.warn("Текст посылок пустой.");
            throw new IllegalArgumentException("Аргумент с посылками пуст");
        }

        List<Parcel> parcels = new ArrayList<>();
        String[] names = parcelsText.split(",");

        for (String name : names) {
            String trimmedName = name.trim();
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