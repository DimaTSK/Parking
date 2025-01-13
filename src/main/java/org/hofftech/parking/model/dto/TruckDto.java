package org.hofftech.parking.model.dto;

import lombok.Data;

import java.util.List;


@Data
public class TruckDto {
    private int truckId;
    private String truckSize;
    private List<ParcelDto> packages;
}