package org.hofftech.parking.utill;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.dto.ParcelDto;

import java.util.List;

@Slf4j
public class ParcelSorter {
    public static void sortParcels(List<ParcelDto> parcelDtoList) {
        parcelDtoList.sort(ParcelSorter::compareParcels);
        log.info("Упаковки отсортированы по высоте и ширине.");
    }

    private static int compareParcels(ParcelDto a, ParcelDto b) {
        int heightDiff = Integer.compare(b.getType().getHeight(), a.getType().getHeight());
        if (heightDiff == 0) {
            return Integer.compare(b.getType().getWidth(), a.getType().getWidth());
        }
        return heightDiff;
    }
}