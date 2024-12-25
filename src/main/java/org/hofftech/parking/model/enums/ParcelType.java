package org.hofftech.parking.model.enums;

import lombok.Getter;

import java.util.List;

@Getter
public enum ParcelType {
    ONE(List.of("1")),
    TWO(List.of("22")),
    THREE(List.of("333")),
    FOUR(List.of("4444")),
    FIVE(List.of("55555")),
    SIX(List.of("666", "666")),
    SEVEN(List.of("777", "7777")),
    EIGHT(List.of("8888", "8888")),
    NINE(List.of("999", "999", "999"));

    private final List<String> shape;

    ParcelType(List<String> shape) {
        this.shape = shape;
    }

    public int getWidth() {
        return shape.get(0).length();
    }

    public int getHeight() {
        return shape.size();
    }

    public static ParcelType fromShape(List<String> shape) {
        for (ParcelType type : values()) {
            if (type.shape.equals(shape)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Нет соответствующего ParcelType для формы: " + shape);
    }
}