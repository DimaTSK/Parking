package org.hofftech.parking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Package {
    private final PackageType type;
    private final int id;
    private PackageStartPosition packageStartPosition;
}