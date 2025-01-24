package org.hofftech.parking.model.enums;

/**
 * Перечисление {@code OrderOperationType} определяет тип операции заказа,
 * которая может быть либо загрузкой, либо разгрузкой.
 *
 * <p>
 * Используется для указания типа операции, связанной с заказом,
 * что влияет на расчет стоимости и поведение связанных с ним методов.
 * </p>
 */
public enum OrderOperationType {
    LOAD,
    UNLOAD
}