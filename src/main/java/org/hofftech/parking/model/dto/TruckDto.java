package org.hofftech.parking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class TruckDto {
    private int truckId;
    private String truckSize;
    private List<ParcelDto> parcels;
}