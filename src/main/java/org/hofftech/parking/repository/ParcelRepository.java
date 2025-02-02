package org.hofftech.parking.repository;

import org.hofftech.parking.exception.ParcelNameException;
import org.hofftech.parking.exception.ParcelNotFoundException;
import org.hofftech.parking.model.enums.DefaultParcelType;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParcelStartPosition;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Репозиторий для управления посылками.
 * Предоставляет методы для добавления, поиска, редактирования, удаления
 * посылок, а также загрузки посылок по умолчанию.
 */
public class ParcelRepository {
    private static final int FIRST_CHAR = 0;
    private static final int START_POSITION_X = 0;
    private static final int START_POSITION_Y = 0;

    private final Map<String, Parcel> parcels = new HashMap<>();

    /**
     * Добавляет новую посылку в репозиторий.
     *
     * @param providedParcel посылка для добавления
     * @throws ParcelNameException если посылка с таким именем уже существует
     */
    public void addParcel(Parcel providedParcel) {
        if (parcels.containsKey(providedParcel.getName())) {
            throw new ParcelNameException("Посылка с таким именем уже существует: " + providedParcel.getName());
        }
        parcels.put(providedParcel.getName(), providedParcel);
    }

    /**
     * Ищет посылку по имени, игнорируя регистр.
     *
     * @param name имя посылки для поиска
     * @return {@code Optional} с найденной посылкой или пустой {@code Optional}, если посылка не найдена
     */
    public Optional<Parcel> findParcel(String name) {
        for (String key : parcels.keySet()) {
            if (key.equalsIgnoreCase(name)) {
                return Optional.ofNullable(parcels.get(key));
            }
        }
        return Optional.empty();
    }

    /**
     * Редактирует существующую посылку.
     *
     * @param name          имя посылки для редактирования
     * @param updatedParcel обновленная посылка
     * @throws ParcelNotFoundException если посылка с данным именем не найдена
     */
    public void editParcel(String name, Parcel updatedParcel) {
        if (!parcels.containsKey(name)) {
            throw new ParcelNotFoundException("Посылка не найдена: " + name);
        }
        parcels.put(name, updatedParcel);
    }

    /**
     * Удаляет посылку по имени.
     *
     * @param name имя посылки для удаления
     * @throws ParcelNotFoundException если посылка с данным именем не найдена
     */
    public void deleteParcel(String name) {
        if (!parcels.containsKey(name)) {
            throw new ParcelNotFoundException("Посылка не найдена: " + name);
        }
        parcels.remove(name);
    }

    /**
     * Возвращает список всех посылок, отсортированных по имени.
     *
     * @return список всех посылок
     */
    public List<Parcel> findAllParcel() {
        return parcels.values().stream()
                .sorted(Comparator.comparing(Parcel::getName))
                .toList();
    }

    /**
     * Загружает посылки по умолчанию в репозиторий.
     * Создает и добавляет посылки на основе типов из {@link DefaultParcelType}.
     */
    public void loadDefaultParcels() {
        int counter = 1;
        for (DefaultParcelType type : DefaultParcelType.values()) {
            String name = "Посылка Тип " + counter++;
            parcels.put(name, new Parcel(
                    name,
                    type.getShape(),
                    type.getShape().getFirst().charAt(FIRST_CHAR),
                    new ParcelStartPosition(START_POSITION_X, START_POSITION_Y)
            ));
        }
    }
}
