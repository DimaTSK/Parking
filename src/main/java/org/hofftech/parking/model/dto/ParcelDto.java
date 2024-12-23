package org.hofftech.parking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hofftech.parking.model.enums.ParcelType;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ParcelDto {
    private final ParcelType type;
    private final int id;
    private ParcelPositionDto parcelPositionDto;
}