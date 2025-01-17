package org.hofftech.parking.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class ParcelStartPosition {
    private final int x;
    private final int y;
}