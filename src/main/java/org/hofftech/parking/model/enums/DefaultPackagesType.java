package org.hofftech.parking.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum DefaultPackagesType {
    ONE(List.of("1")),
    TWO(List.of("22")),
    THREE(List.of("333")),
    FOUR(List.of("4444")),
    FIVE(List.of("55555")),
    SIX(Arrays.asList("666", "666")),
    SEVEN(Arrays.asList("777", "7777")),
    EIGHT(Arrays.asList("8888", "8888")),
    NINE(Arrays.asList("999", "999", "999"));

    private final List<String> shape;
}