package org.hofftech.parking.parcer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.ParcelArgumentException;
import org.hofftech.parking.exception.ParcelNotFoundException;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.repository.ParcelRepository;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class ParsingService {
    private static final String COMMA_REPLACEMENTS = "[“”\"]";
    private static final String PARCELS_SPLITTER = ",";
    private final ParcelRepository parcelRepository;

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