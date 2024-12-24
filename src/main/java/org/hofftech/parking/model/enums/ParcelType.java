package org.hofftech.parking.model.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final Map<List<String>, ParcelType> SHAPE_MAP = new HashMap<>();
    private final List<String> shape;

    static {
        for (ParcelType type : values()) {
            SHAPE_MAP.put(type.shape, type);
        }
    }

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
        ParcelType type = SHAPE_MAP.get(shape);
        if (type == null) {
            throw new IllegalArgumentException("Нет соответствующего ParcelType для формы: " + shape);
        }
        return type;
    }

    public List<String> getShape() {
        if (SEVEN.equals(this)) {
            List<String> reversedShape = new ArrayList<>(this.shape);
            Collections.reverse(reversedShape);
            return reversedShape;
        } else {
            return this.shape;
        }
    }
}
