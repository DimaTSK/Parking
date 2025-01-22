package org.hofftech.parking.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ParcelDto {

    private String name;
    private List<String> shape;
    private char symbol;
    private PositionDto startPosition;
}