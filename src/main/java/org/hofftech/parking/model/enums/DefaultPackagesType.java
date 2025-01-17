package org.hofftech.parking.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * Перечисление типов пакетов по умолчанию с их соответствующими формами.
 * Каждому типу пакета соответствует список строк, представляющих его форму.
 */
@Getter
@RequiredArgsConstructor
public enum DefaultPackagesType {

    /**
     * Пакет типа ONE с формой "1".
     */
    ONE(List.of("1")),

    /**
     * Пакет типа TWO с формой "22".
     */
    TWO(List.of("22")),

    /**
     * Пакет типа THREE с формой "333".
     */
    THREE(List.of("333")),

    /**
     * Пакет типа FOUR с формой "4444".
     */
    FOUR(List.of("4444")),

    /**
     * Пакет типа FIVE с формой "55555".
     */
    FIVE(List.of("55555")),

    /**
     * Пакет типа SIX с формой "666" на двух строках.
     */
    SIX(Arrays.asList("666", "666")),

    /**
     * Пакет типа SEVEN с формой "777" и "7777" на двух строках.
     */
    SEVEN(Arrays.asList("777", "7777")),

    /**
     * Пакет типа EIGHT с формой "8888" на двух строках.
     */
    EIGHT(Arrays.asList("8888", "8888")),

    /**
     * Пакет типа NINE с формой "999" на трёх строках.
     */
    NINE(Arrays.asList("999", "999", "999"));

    /**
     * Список строк, представляющих форму пакета.
     */
    private final List<String> shape;
}
